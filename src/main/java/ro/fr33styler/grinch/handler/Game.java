package ro.fr33styler.grinch.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.api.GameEndEvent;
import ro.fr33styler.grinch.api.GameStartEvent;
import ro.fr33styler.grinch.cache.PlayerStatus;
import ro.fr33styler.grinch.handlerutils.MathUtils;
import ro.fr33styler.grinch.mysql.MySQL;
import ro.fr33styler.grinch.scoreboard.ScoreboardStatus;
import ro.fr33styler.grinch.song.Song;

public class Game {

	private Song song;
	private final int id;
	private final int min;
	private final int max;
	private int timer = 10;
	private final Main main;
	private boolean isStoping;
	private final Location lobby;
	private boolean isStarted;
	private List<Player> top = new ArrayList<Player>();
	private GameState state = GameState.WAITING;
	private List<Location> signs = new ArrayList<Location>();
	private List<Player> players = new ArrayList<Player>();
	private List<Block> gifts = new ArrayList<Block>();
	private List<Song> songs = new ArrayList<Song>();
	private List<FireworkEffect> effects = new ArrayList<FireworkEffect>();
	private List<Skull> gifts_rollback = new ArrayList<Skull>();
	private HashMap<Player, Double> scores = new HashMap<Player, Double>();
    private HashMap<UUID, ScoreboardStatus> status = new HashMap<UUID, ScoreboardStatus>();
	private HashMap<UUID, PlayerStatus> stats = new HashMap<UUID, PlayerStatus>();
	
	public Game(Main main, int id, Location lobby, int min, int max, List<Block> gifts) {
		this.id = id;
		this.min = min;
		this.max = max;
		this.main = main;
		this.timer = main.getConfiguration().getInt("Game.WaitTime");
		this.lobby = lobby;
		this.gifts  = gifts;
		Builder b = FireworkEffect.builder().trail(false).flicker(false);
		effects.add(b.withColor(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE).build());
		effects.add(b.withColor(Color.BLUE, Color.AQUA).build());
		effects.add(b.withColor(Color.GREEN, Color.LIME).build());
	}

	public int getID() {
		return id;
	}
	
	public Main getMain() {
		return main;
	}
	
	public Location getLobby() {
		return lobby;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getTimer() {
		return timer;
	}
	
	public Song getSong() {
		return song;
	}
	
	public List<Song> getSongs() {
		return songs;
	}
	
	public void setSong(Song song) {
		this.song = song;
	}
	
	public List<Skull> restoreGifts() {
		return gifts_rollback;
	}

	public List<Location> getSigns() {
		return signs;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	public void addSign(Location sign) {
		signs.add(sign);
	}

	public HashMap<Player, Double> getScores() {
		return scores;
	}
	
	public HashMap<UUID, PlayerStatus> getStats() {
		return stats;
	}
	
	public List<Block> getGifts() {
		return gifts;
	}
	
	public boolean isStoping() {
		return isStoping;
	}
	
	public void isStoping(boolean value) {
		isStoping = value;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setGameTimer(int timer) {
		this.timer = timer;
	}
	
	public int getAllGiftsStolen() {
		Double gifts = 0.0;
		if (scores.size() > 0) {
		   for (Double i : scores.values()) {
			   gifts = gifts+i;
		   }
		}
		return gifts.intValue();
	}
	
	public HashMap<UUID, ScoreboardStatus> getStatus() {
	     return status;
   }
	
	public void setState(GameState state) {
		this.state = state;
		main.getManager().updateSigns(this);
	}
	
	public void start() {
		isStarted = true;
		for (Song s : main.getSongs()) {
			songs.add(new Song(s));
		}
		for (Player p : players) {
			for (String str : main.getCmdsStart()) {
				main.getServer().dispatchCommand(Bukkit.getConsoleSender(), str.replace("%player%", p.getName()));
			}
		}
	}
	
	public void broadcast(String message) {
		for (Player p : players) {
			p.sendMessage(message.replace("&", "§"));
		}
	}

	public void refreshTop() {
		top.clear();
		for (int x = 0; x < players.size(); x++) {
			double best = 0.0;
			Player name = null;
			for (Entry<Player, Double> entry : scores.entrySet()) {
				if (entry.getValue() > best && !top.contains(entry.getKey())) {
					best = entry.getValue();
					name = entry.getKey();
				}
			}
			top.add(name);
		}
	}
	
	public Integer getRank(Player p) {
		int top = 1;
		for (Player pl : this.top) {
			if (pl == p) {
				return top;
			}
			top++;
		}
		return null;
	}
	
	public List<Player> getTop() {
		return top;
	}
	
	public void stop() {
		isStarted = false;
		song = null;
		songs.clear();
		top.clear();
		timer = main.getConfiguration().getInt("Game.WaitTime");
	}
	
	public void run() {
		if (state == GameState.END) {
			if (timer == 0) {
				main.getManager().stopGame(this, main.getConfiguration().getBoolean("Game.AutoJoinOnEnd"));
				return;
			}
			for (int x = 0; x < 3; x++) {
				if (top.size() <= x+1) {
					break;
				}
				Player p = top.get(x);
				Firework f = p.getWorld().spawn(p.getLocation(), Firework.class);
				FireworkMeta fm = f.getFireworkMeta();
				fm.addEffect(effects.get(MathUtils.random().nextInt(effects.size())));
				f.setFireworkMeta(fm);
			}
		} else if (state == GameState.IN_GAME) {
			if (timer == 30) {
				players.forEach(p -> p.sendTitle("§630", Messages.TITLE_REMAINING.toString(), 0, 45, 0));
			}
			if (timer > 0 && timer <= 5) {
				players.forEach(p -> p.sendTitle("§c" + timer, Messages.TITLE_REMAINING.toString(), 0, 45, 0));
			}
			if (timer == 0) {
				timer = 5;
				state = GameState.END;
				List<String> commands = main.getConfiguration().getStringList("Game.CommandsWin");
				for (Player p : players) {
					int won = 0;
					if (p == top.get(0)) {
						won = 1;
						for (String command : commands) {
							if (command.contains("%first%")) {
							    main.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%first%", p.getName()));
							}
						}
						p.sendTitle(Messages.GAME_WON.toString(), Messages.TITLE_FIRST.toString(), 0, 100, 0);
					} else if (p == top.get(1)) {
						for (String command : commands) {
							if (command.contains("%second%")) {
							    main.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%second%", p.getName()));
							}
						}
						p.sendTitle(Messages.GAME_OVER.toString(), Messages.TITLE_SECOND.toString(), 0, 100, 0);
					} else if (top.size() > 2 && p == top.get(2)) {
						for (String command : commands) {
							if (command.contains("%third%")) {
							    main.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%third%", p.getName()));
							}
						}
						p.sendTitle(Messages.GAME_OVER.toString(), Messages.TITLE_THIRD.toString(), 0, 100, 0);
					} else {
						for (String command : commands) {
							if (command.contains("%other%")) {
							    main.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%other%", p.getName()));
							}
						}
						p.sendTitle(Messages.GAME_OVER.toString(), Messages.TITLE_OVER.toString().replace("%rank%", ""+getRank(p)), 0, 100, 0);
					}
				    MySQL mysql = main.getMySQL();
				    if (mysql != null) {
					  mysql.getCache().add(new PlayerStatus(p.getUniqueId(), p.getName(), scores.get(p).intValue(), won));
				    }
					p.sendMessage("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					p.sendMessage(Messages.GAME_NAME.toString());
					p.sendMessage("");
					if (top.size() > 0) {
						p.sendMessage(Messages.GAME_TOP.toString().replace("%place%", "1").replace("%player%", top.get(0).getName()).replace("%gifts%", ""+scores.get(top.get(0)).intValue()));
					}
					if (top.size() > 1) {
						p.sendMessage(Messages.GAME_TOP.toString().replace("%place%", "2").replace("%player%", top.get(1).getName()).replace("%gifts%", ""+scores.get(top.get(1)).intValue()));
					}
					if (top.size() > 2) {
						p.sendMessage(Messages.GAME_TOP.toString().replace("%place%", "3").replace("%player%", top.get(2).getName()).replace("%gifts%", ""+scores.get(top.get(2)).intValue()));
					}
					p.sendMessage("");
					p.sendMessage("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
				}
				GameEndEvent event = new GameEndEvent(top, players);
				main.getServer().getPluginManager().callEvent(event);
			}
		} else if (state == GameState.WAITING) {
			if (players.size() < min) {
				stop();
				for (Player p : players) {
					p.sendMessage(Messages.PREFIX + " " + Messages.NOT_ENOUGH_PLAYERS);
				}
				timer = main.getConfiguration().getInt("Game.WaitTime");
			} else if (timer == 0) {
				timer = main.getConfiguration().getInt("Game.GameTime");
				if (songs.size() > 0) {
				    song = songs.get(MathUtils.random().nextInt(songs.size()));
				}
				for (Player p : players) {
					p.setWalkSpeed(0.35f);
					p.getInventory().setItem(0, new ItemStack(Material.SNOWBALL, main.getConfiguration().getInt("Game.Snowballs")));
					p.sendMessage("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					p.sendMessage(Messages.GAME_NAME.toString());
					p.sendMessage("");
					String[] message = Messages.GAME_START_MESSAGE.toString().split("#");
					for (String m : message) {
						p.sendMessage("➢ "+m);
					}
					p.sendMessage("");
					p.sendMessage("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					p.getInventory().setItem(8, null);
					p.playSound(p.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2F, 2F);
				}
				refreshTop();
				setState(GameState.IN_GAME);
				status.values().forEach(s -> s.reset());
				GameStartEvent e = new GameStartEvent(players);
				main.getServer().getPluginManager().callEvent(e);
			} else {
			  for (Player p : players) {
				 if (timer <= 5) {
				    p.playSound(p.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 0.5F);
				    p.sendMessage(Messages.PREFIX + " " + Messages.GAME_START.toString().replace("%timer%", timer + ""));
				 } else if (timer % 10 == 0) {
				    p.sendMessage(Messages.PREFIX + " " + Messages.GAME_START.toString().replace("%timer%", timer + ""));
				 }
			  }
		   }
		}
		timer--;
	}
	
}