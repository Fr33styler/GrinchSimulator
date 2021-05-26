package ro.fr33styler.grinch.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class GameSetup {
	
	private int id;
	private int min;
	private int max;
	private int step = 0;
	private Location lobby;
	private List<Block> gifts;
	
	public GameSetup(int id, int min, int max) {
		this.id = id;
		gifts = new ArrayList<Block>();
		this.min = min;
		this.max = max;
	}
	
	public int getID() {
		return id;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public void nextStep() {
		step++;
	}
	
	public int getStep() {
		return step;
	}
	
	public Location getLobby() {
		return lobby;
	}
	
	public void setLobby(Location lobby) {
		this.lobby = lobby;
	}
	
	public List<Block> getGifts() {
		return gifts;
	}
	
}