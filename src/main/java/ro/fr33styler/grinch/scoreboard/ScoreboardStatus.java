package ro.fr33styler.grinch.scoreboard;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardStatus {

	private Player p;
	private Objective obj;
	private Scoreboard board;
	private HashMap<Integer, ScoreboardLine> entries;
	
	public ScoreboardStatus(Player p) {
		this.p = p;
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		obj = board.registerNewObjective("status", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		entries = new HashMap<Integer, ScoreboardLine>();
		p.setScoreboard(board);
	}
    
	public Player getPlayer() {
		return p;
	}
	
	public Objective getObjective() {
		return obj;
	}

	public void setTitle(String title) {
		obj.setDisplayName(title);
	}
	
	public void reset() {
		entries.values().forEach(l -> l.unregister());
		entries.clear();
	}
	
	public void updateLine(int line, String text) {
		if (entries.get(line) != null) {
			entries.get(line).update(text);
		} else {
			entries.put(line, new ScoreboardLine(board, obj, text, line, line));
		}
	}
	
	public void updateLine(int line, int score, String text) {
		if (entries.get(line) != null) {
			entries.get(line).update(text);
		} else {
			entries.put(line, new ScoreboardLine(board, obj, text, line, score));
		}
	}
}