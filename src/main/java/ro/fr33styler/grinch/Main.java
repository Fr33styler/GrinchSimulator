package ro.fr33styler.grinch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import ro.fr33styler.grinch.configuration.Configuration;
import ro.fr33styler.grinch.handler.Game;
import ro.fr33styler.grinch.handler.GameListener;
import ro.fr33styler.grinch.handler.GameManager;
import ro.fr33styler.grinch.handler.GameSetup;
import ro.fr33styler.grinch.handlerutils.GameUtils;
import ro.fr33styler.grinch.mysql.MySQL;
import ro.fr33styler.grinch.song.NBSDecoder;
import ro.fr33styler.grinch.song.Song;

public class Main extends JavaPlugin {
	
	private MySQL mysql;
	private UpdateTask update;
	private GameManager manager;
	private Configuration config;
	private Configuration messages;
	private Configuration database;
	private List<String> cmdsStart;
	private HashMap<Player, GameSetup> setup;
	private List<Song> songs = new ArrayList<Song>();
	
	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		ConsoleCommandSender console = getServer().getConsoleSender();
		console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		console.sendMessage("§aGrinchSimulator plugin is loading... ");
		console.sendMessage("§a - Version: " + getDescription().getVersion());
		console.sendMessage("§a - Author: Fr33styler");
		config = new Configuration(this, "config.yml", false);
		cmdsStart = config.getStringList("Game.CommandsStart");
		if (cmdsStart == null) {
			cmdsStart = new ArrayList<>();
		}
		messages = new Configuration(this, "messages.yml", true);
		for (Messages msg : Messages.values()) {
			if (messages.getString("Messages." + msg.name()) == null) {
				messages.set("Messages." + msg.name(), msg.toString().replace("§", "&"));
			}
		}
		for (String name : messages.getConfigurationSection("Messages").getKeys(false)) {
			Messages msg = Messages.getEnum(name);
			if (msg == null) {
				messages.set("Messages." + name, null);
			} else {
				msg.setMessage(messages.getString("Messages." + name));
			}
		}
		messages.save();
		update = new UpdateTask(this);
		setup = new HashMap<Player, GameSetup>();
		manager = new GameManager(this, config.getBoolean("BungeeMode.Enabled"));
		database = new Configuration(this, "database.yml", false);
		if (database.getString("Game") != null && !database.isString("Game")) {
			for (String ID : database.getConfigurationSection("Game").getKeys(false)) {
				try {
					int min = database.getInt("Game." + ID + ".Min");
					int max = database.getInt("Game." + ID + ".Max");
					Location lobby = GameUtils.getDeserializedLocation(database.getString("Game." + ID + ".Lobby"), 1);
					List<Block> gifts = GameUtils.getDeserializedBlocks(database.getStringList("Game." + ID + ".Gifts"));
					manager.addGame(new Game(this, Integer.parseInt(ID), lobby, min, max, gifts));
				} catch (Exception e) {
					console.sendMessage("§c - Error loading the game with ID: " + ID);
				}
			}
		}
		Location l = new Location(null, 0, 0, 0);
		for (String sign : database.getStringList("Signs")) {
			String[] split = sign.split(",");
			int id = Integer.parseInt(split[0]);
			String world = split[1];
			int x = Integer.parseInt(split[2]);
			int y = Integer.parseInt(split[3]);
			int z = Integer.parseInt(split[4]);
			l.setWorld(Bukkit.getWorld(world));
			l.setX(x);
			l.setY(y);
			l.setZ(z);
			if (l.getWorld() != null) {
			    Game g = manager.getGame(id);
			    Block block = l.getBlock();
			    if (g != null && (block.getBlockData() instanceof WallSign)) {
				    g.getSigns().add(l.getBlock().getLocation());
			   }
			}
		}
		console.sendMessage("§a - Loading songs...");
	    File folder = new File(getDataFolder().getPath() + "/Songs/");
	    if (!folder.exists()) {
	    	saveResource("Songs/0.nbs", true);
	    	saveResource("Songs/1.nbs", true);
	    	saveResource("Songs/2.nbs", true);
	    	saveResource("Songs/3.nbs", true);
	    	saveResource("Songs/4.nbs", true);
	    	saveResource("Songs/5.nbs", true);
	    }
	    for (File file : new File(getDataFolder().getPath() + "/Songs/").listFiles()) {
	      if (file.getName().endsWith(".nbs")) {
	    	 songs.add(NBSDecoder.parse(file));
	      }
	    }
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		getCommand("grinch").setExecutor(new Commands(this));
		boolean enabled = config.getBoolean("MySQL.Enabled");
		if (enabled) console.sendMessage("§a - Loading MySQL...");
		int queueAmount = config.getInt("MySQL.QueueAmount");
		String host = config.getString("MySQL.Host");
		String database = config.getString("MySQL.Database");
		String username = config.getString("MySQL.Username");
		String password = config.getString("MySQL.Password");
		int port = config.getInt("MySQL.Port");
		mysql = enabled ? new MySQL(this, host, database, username, password, port, queueAmount) : null;
		console.sendMessage("§aGrinchSimulator has been loaded!");
		console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
	}
	
	@Override
	public void onDisable() {
		for (Game g : manager.getGames()) {
			manager.stopGame(g, false);
		}
		setup.clear();
		manager = null;
		HandlerList.unregisterAll(this);
		update.cancel();
		update = null;
	}

	public List<Song> getSongs() {
		return songs;
	}
	
	public List<String> getCmdsStart() {
		return cmdsStart;
	}
	
	public Configuration getConfiguration() {
		return config;
	}
	
	public MySQL getMySQL() {
		return mysql;
	}
	
	public GameManager getManager() {
		return manager;
	}

	public UpdateTask getUpdateTask() {
		return update;
	}

	public HashMap<Player, GameSetup> getSetups() {
		return setup;
	}
	
	public Configuration getGameDatabase() {
		return database;
	}
	
}