package ro.fr33styler.grinch.cache;

import java.util.UUID;

public class PlayerStatus {

	private UUID uuid;
	private String name;
	private int score;
	private int won;
	
	public PlayerStatus(UUID uuid, String name, int score, int won) {
		this.uuid = uuid;
		this.name = name;
		this.score = score;
		this.won = won;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public int hasWon() {
		return won;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
	
}
