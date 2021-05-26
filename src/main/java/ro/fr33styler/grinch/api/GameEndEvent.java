package ro.fr33styler.grinch.api;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

	private List<Player> top;
	private List<Player> players;
	private static final HandlerList handlers = new HandlerList();

	public GameEndEvent(List<Player> top, List<Player> players) {
		this.top = top;
		this.players = players;
	}

	public List<Player> getTop() {
		return top;
	}

	public List<Player> getPlayers() {
		return players;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
