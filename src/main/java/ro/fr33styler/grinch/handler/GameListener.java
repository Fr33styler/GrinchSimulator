package ro.fr33styler.grinch.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.api.GameLeaveEvent;
import ro.fr33styler.grinch.handlerutils.GameUtils;

public class GameListener implements Listener {

	private Main main;
	public GameListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		GameSetup setup = main.getSetups().get(p);
		if (setup != null && setup.getStep() == 1) {
		    if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
		    	if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.PLAYER_HEAD) {
		    		if (!setup.getGifts().contains(e.getClickedBlock())) {
		    		    setup.getGifts().add(e.getClickedBlock());
		    		    p.sendMessage(Messages.PREFIX + "§7 Gift was set. (§d"+setup.getGifts().size()+"§7)");
		    		} else {
		    			p.sendMessage(Messages.PREFIX + "§c This gift has been already selected.");
		    		}
		    	}
		    }
		}
		
		Game g = main.getManager().getGame(p);
		if (g != null) {
			if (g.getState() != GameState.IN_GAME) {
				e.setCancelled(true);
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (g.getState() == GameState.WAITING && p.getInventory().getItemInHand().getType() == Material.RED_BED) {
						main.getManager().removePlayer(p, g, false, false);
						p.sendMessage(Messages.PREFIX + " " + Messages.GAME_LEFT);
					}
				}
			} else {
				if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.PLAYER_HEAD) {
					if (g.getGifts().contains(e.getClickedBlock())) {
					    g.restoreGifts().add((Skull) e.getClickedBlock().getState());
					    e.getClickedBlock().setType(Material.AIR);
					    p.sendMessage(Messages.GAME_YOU_STOLE.toString());
					    g.getScores().put(p, g.getScores().get(p)+1);
					    p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
					    Location gift = e.getClickedBlock().getLocation();
					    p.spawnParticle(Particle.HEART, gift, 5, 0.2f, 0.2f, 0.2f);
					    p.spawnParticle(Particle.NOTE, gift, 5, 0.2f, 0.2f, 0.2f);
					}
				}
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof Sign) {
			Location s = e.getClickedBlock().getLocation();
			for (Game game : main.getManager().getGames()) {
				for (Location sign : game.getSigns()) {
					if (s.getWorld() == sign.getWorld() && s.distance(sign) == 0) {
						main.getManager().addPlayer(game, p);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Game g = main.getManager().getGame(p);
		if (e.isCancelled() || !main.getConfiguration().getBoolean("Game.PrivateChat")) {
			return;
		}
		if (g == null) {
			for (Game game : main.getManager().getGames()) {
				e.getRecipients().removeAll(game.getPlayers());
			}
		} else {
			e.setCancelled(true);
			g.broadcast("§7" + p.getName() + "§f: " + e.getMessage());
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Game g = main.getManager().getGame(p);
		if (g != null) {
			if (g.getState() == GameState.WAITING && e.getTo().getWorld() == g.getLobby().getWorld()) {
				if (e.getTo().distance(g.getLobby()) > main.getConfig().getInt("Game.LobbyMaxDistance")) {
					p.setVelocity(g.getLobby().toVector().subtract(p.getLocation().toVector()).normalize());
				}
			} else if (g.getState() == GameState.IN_GAME) {
				if (e.getTo().getBlockY() <= 0) {
					p.teleport(g.getLobby());
				}
			}
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		if (main.getManager().isBungeeMode()) {
			Game g = main.getManager().getGames().get(0);
			e.setMotd(g.getState().getState());
			e.setMaxPlayers(g.getMax());
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (main.getManager().isBungeeMode()) {
			Game g = main.getManager().getGames().get(0);
			if (g.getState() != GameState.WAITING) {
				e.disallow(Result.KICK_OTHER, Messages.GAME_HAS_STARTED.toString());
			} else if (g.getPlayers().size() >= g.getMax()) {
				e.disallow(Result.KICK_FULL, Messages.GAME_IS_FULL.toString());
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (main.getManager().isBungeeMode()) {
			e.setJoinMessage(null);
			Game g = main.getManager().getGames().get(0);
			main.getManager().addPlayer(g, e.getPlayer());
		}
	}
	
	@EventHandler
	public void onLeave(GameLeaveEvent e) {
		if (main.getManager().isBungeeMode()) {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(data);
			try {
				out.writeUTF("Connect");
				out.writeUTF(main.getConfiguration().getString("BungeeMode.ServerOnGameEnd"));
				e.getPlayer().sendPluginMessage(main, "BungeeCord", data.toByteArray());
				out.close();
				data.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		Game g = main.getManager().getGame(p);
		if (g != null && !p.hasPermission("gs.bypass")) {
			String[] split = e.getMessage().split(" ");
			String cmd = split[0];
			if (cmd.equalsIgnoreCase("/leave") || cmd.equalsIgnoreCase("/quit")) {
				e.setCancelled(true);
				p.sendMessage(Messages.PREFIX + " " + Messages.GAME_LEFT.toString());
				if (main.getManager().isBungeeMode()) {
					main.getManager().removePlayer(p, g, true, false);
				} else {
					main.getManager().removePlayer(p, g, false, false);
				}
			} else if (!cmd.equalsIgnoreCase("/gs") && !cmd.equalsIgnoreCase("/grinch") && !GameUtils.containsIgnoreCase(main.getConfiguration().getStringList("Game.Whitelist"), cmd)) {
				e.setCancelled(true);
				p.sendMessage(Messages.PREFIX + " " + Messages.RESTRICTED_COMMAND.toString());
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Game g = main.getManager().getGame(p);
		if (g != null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		Game g = main.getManager().getGame(p);
		if (g != null) {
			ItemStack is = e.getItemDrop().getItemStack();
			ItemStack i = is.clone();
			e.getItemDrop().remove();
			p.getInventory().setItem(p.getInventory().getHeldItemSlot(), i);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (main.getManager().getGame(p) != null) {
			e.setCancelled(true);
			return;
		}
		if (e.getBlock().getState() instanceof Sign && p.hasPermission("gs.admin")) {
			Location s = e.getBlock().getLocation();
			for (Game g : main.getManager().getGames()) {
				Iterator<Location> it = g.getSigns().iterator();
				while (it.hasNext()) {
					Location sign = it.next();
					if (s.getWorld() == sign.getWorld() && s.distance(sign) == 0) {
						p.sendMessage(Messages.PREFIX + " §cSign removed succefully!");
						String key = g.getID() + "," + s.getWorld().getName() + "," + s.getBlockX() + "," + s.getBlockY() + "," + s.getBlockZ();
						List<String> keys = main.getGameDatabase().getStringList("Signs");
						keys.remove(key);
						main.getGameDatabase().set("Signs", keys);
						main.getGameDatabase().save();
						it.remove();
						return;
					}
				}
			}
		}
	}
	
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
        	for (Game g : main.getManager().getGames()) {
        		if (g.getLobby().getWorld() == e.getWorld()) {
        			e.setCancelled(true);
        			break;
        		}
        	}
        }
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		Game g = main.getManager().getGame(p);
		if (g != null) {
			main.getManager().removePlayer(p, g, true, false);
		}
		GameSetup setup = main.getSetups().get(p);
		if (setup != null) {
			main.getSetups().remove(p);
		}
		if (main.getManager().isBungeeMode()) {
			e.setLeaveMessage(null);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Game g = main.getManager().getGame(p);
		if (g != null) {
			main.getManager().removePlayer(p, g, true, false);
		}
		GameSetup setup = main.getSetups().get(p);
		if (setup != null) {
			main.getSetups().remove(p);
		}
		if (main.getManager().isBungeeMode()) {
			e.setQuitMessage(null);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			if (main.getManager().getGame(p) != null) {
				if (e.getCause() == DamageCause.PROJECTILE) {
					e.setDamage(2);
					p.setHealth(p.getMaxHealth());
					p.addPotionEffect(PotionEffectType.SLOW.createEffect(4, 3));
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER) {
			if (main.getManager().getGame((Player) e.getEntity()) != null) {
				e.setFoodLevel(20);
			}
		}
	}

	@EventHandler
	public void onHealthRegain(EntityRegainHealthEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER) {
			if (main.getManager().getGame((Player) e.getEntity()) != null) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onSignPlace(SignChangeEvent e) {
		Player p = e.getPlayer();
		if (e.getLine(0).equals("[Grinch]") && p.hasPermission("gs.admin")) {
			try {
				int line = Integer.valueOf(e.getLine(1));
				Game g = main.getManager().getGame(line);
				if (g != null) {
					boolean sign_glass = main.getConfiguration().getBoolean("Game.SignGlass");
					Location l = e.getBlock().getLocation();
					g.getSigns().add(l);
					if (sign_glass) {
						org.bukkit.material.Sign sign = (org.bukkit.material.Sign)l.getBlock().getState().getData();
						Block attached = l.getBlock().getRelative(sign.getAttachedFace());
						if (g.getState() == GameState.WAITING) {
							attached.setType(Material.GREEN_STAINED_GLASS);
						} else {
							attached.setType(Material.RED_STAINED_GLASS);
						}
					}
				    e.setLine(0, Messages.SIGN_FIRST.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", 8+""));
				    e.setLine(1, Messages.SIGN_SECOND.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", 8+""));
				    e.setLine(2, Messages.SIGN_THIRD.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", 8+""));
				    e.setLine(3, Messages.SIGN_FOURTH.toString().replace("%prefix%", Messages.PREFIX.toString()).replace("%state%", g.getState().getState()).replace("%min%", g.getPlayers().size()+"").replace("%max%", 8+""));
					List<String> keys = main.getGameDatabase().getStringList("Signs");
					keys.add(line+","+l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ());
					main.getGameDatabase().set("Signs", keys);
					main.getGameDatabase().save();
				    p.sendMessage(Messages.PREFIX + " §aSign created succefully!");
				} else {
					e.setCancelled(true);
					p.sendMessage(Messages.PREFIX + " §cThe game dosen't exist!");
				}
			} catch (Exception ex) {
				p.sendMessage(Messages.PREFIX + " §cInvalid game ID!");
				return;
			}
		}
	}
	
}

