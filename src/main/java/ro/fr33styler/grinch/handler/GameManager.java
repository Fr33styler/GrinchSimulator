package ro.fr33styler.grinch.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.api.GameJoinEvent;
import ro.fr33styler.grinch.api.GameLeaveEvent;
import ro.fr33styler.grinch.cache.PlayerData;
import ro.fr33styler.grinch.handlerutils.ItemBuilder;
import ro.fr33styler.grinch.scoreboard.ScoreboardStatus;

public class GameManager {

	private Main main;
	private int maxl = 0;
	private boolean bungee = false;
	private List<Game> games = new ArrayList<Game>();
	private HashMap<UUID, PlayerData> data = new HashMap<UUID, PlayerData>();
	
	public GameManager(Main main, boolean bungee) {
		this.main = main;
		if (bungee) {
			this.bungee = bungee;
			main.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");
		}
		this.maxl = main.getConfiguration().getInt("Game.MaxSBNameLenght");
		if (maxl == 0) {
			maxl = 16;	
		}
	}
	
	public void addPlayer(Game g, Player p) {
		if (g == null) {
			p.sendMessage(Messages.PREFIX + " " + Messages.GAME_NULL);
		} else if (getGame(p) != null) {
			p.sendMessage(Messages.PREFIX + " " + Messages.GAME_JOIN_ANOTHER_GAME.toString());
		} else if (g.getState() != GameState.WAITING) {
			p.sendMessage(Messages.PREFIX + " " + Messages.GAME_HAS_STARTED.toString());
		} else if (g.getPlayers().size() >= g.getMax()) {
			p.sendMessage(Messages.PREFIX + " " + Messages.GAME_IS_FULL.toString());
		} else {
			GameJoinEvent e = new GameJoinEvent(p);
			main.getServer().getPluginManager().callEvent(e);
			g.getPlayers().add(p);
			p.getInventory().setHeldItemSlot(4);
			data.put(p.getUniqueId(), new PlayerData(p));
			p.teleport(g.getLobby());
			p.setPlayerWeather(WeatherType.DOWNFALL);
			double score = 0.005;
			for (int x = 0; x < g.getPlayers().size(); x++) {
				score = score+0.005;
			}
			g.getScores().put(p, score);
			p.setGameMode(GameMode.SURVIVAL);
			ScoreboardStatus status = new ScoreboardStatus(p);
			status.setTitle(Messages.SCOREBOARD_TITLE.toString());
			g.getStatus().put(p.getUniqueId(), status);
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
			p.getInventory().setItem(8, ItemBuilder.create(Material.RED_BED, 1, Messages.ITEM_LEFTGAME_NAME+" &8(&e"+Messages.ITEM_RIGHT_CLICK+"&8)", Messages.ITEM_LEFTGAME_LORE.toString()));
			p.updateInventory();
			g.broadcast(Messages.PREFIX + " " + Messages.GAME_JOIN.toString().replace("%name%", p.getName()).replace("%size%", String.valueOf(g.getPlayers().size())).replace("%maxsize%", String.valueOf(g.getMax())));
			if (g.getPlayers().size() >= g.getMin()) {
				g.start();
			}
			updateSigns(g);
		}
	}
	
	public void removePlayer(Player p, Game g, boolean quit, boolean lobby) {
		if (lobby) {
			p.setWalkSpeed(0.2f);
			p.teleport(g.getLobby());
			data.get(p.getUniqueId()).reset();
			g.getStatus().get(p.getUniqueId()).reset();
			double score = 0.005;
			for (Player x : g.getPlayers()) {
				score = score+0.005;
				if (x == p) {
					break;
				}
			}
			g.getTop().clear();
			g.getScores().put(p, score);
			p.getInventory().setItem(8,ItemBuilder.create(Material.RED_BED, 1, Messages.ITEM_LEFTGAME_NAME + " &8(&e" + Messages.ITEM_RIGHT_CLICK + "&8)", Messages.ITEM_LEFTGAME_LORE.toString()));
		} else {
			if (!g.isStoping()) {
				g.getPlayers().remove(p);
				if (g.getState() != GameState.WAITING) {
					if (g.getPlayers().size() < 2) {
						stopGame(g, true);
						g.broadcast(Messages.PREFIX + " " + Messages.GAME_NO_PLAYERS);
					}
				}
			}
			p.resetPlayerWeather();
			g.getScores().remove(p);
			g.getTop().remove(p);
			g.getStats().remove(p.getUniqueId());
			ScoreboardStatus status = g.getStatus().remove(p.getUniqueId());
			if (!quit && status != null) {
				status.reset();
			}
			data.remove(p.getUniqueId()).restore(true);
			updateSigns(g);
			if (g.getState() == GameState.WAITING && !g.isStoping()) {
				g.broadcast(Messages.PREFIX + " " + Messages.GAME_LEAVE.toString().replace("%name%", p.getName()).replace("%size%", String.valueOf(g.getPlayers().size())).replace("%maxsize%", String.valueOf(g.getMax())));
			}
			GameLeaveEvent e = new GameLeaveEvent(p);
			main.getServer().getPluginManager().callEvent(e);
		}
		p.updateInventory();
	}
	
	public void updateStatus(Game g, ScoreboardStatus status) {
		Player p = status.getPlayer();
		if (g.getState() == GameState.WAITING) {
			status.updateLine(7, "");
			status.updateLine(6, Messages.SCOREBOARD_LOBBY_ID.toString() + g.getID());
			status.updateLine(5, Messages.SCOREBOARD_LOBBY_PLAYERS + " §a"+g.getPlayers().size());
			status.updateLine(4, "");
			if (g.isStarted()) {
			  status.updateLine(3, Messages.SCOREBOARD_LOBBY_GAME_START + " §c" + g.getTimer());
			} else {
			  status.updateLine(3, Messages.SCOREBOARD_LOBBY_WAITING.toString());
			}
			status.updateLine(2, "");
			status.updateLine(1, Messages.SCOREBOARD_LOBBY_SERVER.toString());
		} else {
			int minutes = (g.getTimer()+1) % 3600;
			if (g.getState() == GameState.END) {
				minutes = 0;
			}
			String timer = ((minutes / 60 < 10) ? "0" : "") + minutes / 60 + ":" + ((minutes % 60 < 10) ? "0" : "") + minutes % 60;
			status.updateLine(12, "");
			status.updateLine(11, Messages.SCOREBOARD_GAME_TIME_LEFT.toString()+timer);
			status.updateLine(10, Messages.SCOREBOARD_GAME_GIFTS_LEFT.toString()+(g.getGifts().size()-g.getAllGiftsStolen()));
			status.updateLine(9, "");
			status.updateLine(8, Messages.SCOREBOARD_GAME_GIFTS_STOLEN.toString()+g.getScores().get(p).intValue());
			status.updateLine(7, Messages.SCOREBOARD_GAME_RANKING.toString()+g.getRank(p));
			status.updateLine(6, "");
			if (g.getTop().size() > 0) {
				status.updateLine(5,Messages.SCOREBOARD_TOP.toString().replace("%place%", "1").replace("%player%", fix(g.getTop().get(0).getName())).replace("%gifts%", "" + g.getScores().get(g.getTop().get(0)).intValue()));
			}
			if (g.getTop().size() > 1) {
				status.updateLine(4, Messages.SCOREBOARD_TOP.toString().replace("%place%", "2").replace("%player%", fix(g.getTop().get(1).getName())).replace("%gifts%", "" + g.getScores().get(g.getTop().get(1)).intValue()));
			}
			if (g.getTop().size() > 2) {
				status.updateLine(3, Messages.SCOREBOARD_TOP.toString().replace("%place%", "3").replace("%player%", fix(g.getTop().get(2).getName())).replace("%gifts%", "" + g.getScores().get(g.getTop().get(2)).intValue()));
			}
			status.updateLine(2, "");
			status.updateLine(1, Messages.SCOREBOARD_LOBBY_SERVER.toString());
		}
	}
	
	public String fix(String player) {
		if (maxl > player.length()) {
			return player;
		}
		return player.substring(0, maxl);
	}
	
	public Game searchGame() {
		int players = 0;
		Game found = null;
		for (Game g : games) {
			int size = g.getPlayers().size();
			if (g.getState() == GameState.WAITING && size < g.getMax() && g.getTimer() > 1) {
				if (found == null) {
					found = g;
					players = size;
				} else if (size > players) {
					found = g;
					players = size;
				}
			}
		}
		return found;
	}
	
	public void stopGame(Game g, boolean lobby) {
		g.isStoping(true);
		g.stop();
		g.restoreGifts().forEach(s -> s.update(true));
		g.restoreGifts().clear();
		for (Player p : g.getPlayers()) {
			removePlayer(p, g, false, lobby);
		}
		if (lobby) {
		   if (!g.isStarted() && g.getPlayers().size() >= g.getMin()) {
			   g.start();
		   }
		} else {
		   g.getPlayers().clear();
		}
		updateSigns(g);
		g.setState(GameState.WAITING);
		g.isStoping(false);
	}
	
	public void updateSigns(Game g) {
		Iterator<Location> it = g.getSigns().iterator();
		while (it.hasNext()) {
			Location l = it.next();
			if (l.getBlock().getState() instanceof Sign) {
				Sign s = (Sign) l.getBlock().getState();
			    s.setLine(0, Messages.SIGN_FIRST.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", g.getMax()+""));
			    s.setLine(1, Messages.SIGN_SECOND.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", g.getMax()+""));
			    s.setLine(2, Messages.SIGN_THIRD.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", g.getMax()+""));
			    s.setLine(3, Messages.SIGN_FOURTH.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", g.getMax()+""));
			    s.update();
			}
		}
	}
	
	public boolean isBungeeMode() {
		return bungee;
	}
	
	public void addGame(Game g) {
		games.add(g);
	}
	
	public Game getGame(int id) {
		for (Game g : games) {
			if (g.getID() == id) {
				return g;
			}
		}
		return null;
	}
	
	public Game getGame(Player p) {
		for (Game g : games) {
			if (g.getPlayers().contains(p)) {
				return g;
			}
		}
		return null;
	}
	
	public List<Game> getGames() {
		return games;
	}
	
	public void removeGame(Game g) {
		for (Location l : g.getSigns()) {
			if (l.getBlock().getState() instanceof Sign) {
			    Sign s = (Sign) l.getBlock().getState();
			    s.setLine(0, "");
			    s.setLine(1, "");
			    s.setLine(2, "");
			    s.setLine(3, "");
			    s.update();
			    List<String> keys = main.getGameDatabase().getStringList("Signs");
			    keys.remove(g.getID() + "," + s.getWorld().getName() + "," + s.getLocation().getBlockX() + "," + s.getLocation().getBlockY() + "," + s.getLocation().getBlockZ());
			    main.getGameDatabase().set("Signs", keys);
			}
		}
		g.getPlayers().clear();
		g.getSigns().clear();
		games.remove(g);
	}
	
}