package ro.fr33styler.grinch.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameJoinEvent extends Event {

	private Player p;
	private static final HandlerList handlers = new HandlerList();

	public GameJoinEvent(Player p) {
		this.p = p;
	}

	public Player getPlayer() {
		return p;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}