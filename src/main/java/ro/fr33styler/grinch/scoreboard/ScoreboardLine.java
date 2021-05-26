package ro.fr33styler.grinch.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardLine {

	private Team team;
	private Score score;
	private String name;
	private Scoreboard board;
	
	public ScoreboardLine(Scoreboard board, Objective obj, String name, int line, int score) {
		String color = ChatColor.values()[line - 1] + "§r";
		team = board.registerNewTeam(color);
		this.score = obj.getScore(color);
		this.score.setScore(score);
		team.addEntry(color);
		this.board = board;
		update(name);
	}

	public void unregister() {
		team.unregister();
		board.resetScores(score.getEntry());
	}

	public void update(String name) {
		if (!name.equals(this.name)) {
			this.name = name;
			String prefix = name.length() >= 16 ? name.substring(0, 16) : name;
			boolean colorMark = false;
			if (prefix.length() > 0 && prefix.charAt(prefix.length()-1) == '§') {
				prefix = prefix.substring(0, prefix.length() - 1);
				colorMark = true;
			}
			team.setPrefix(prefix);
			if (name.length() > 16) {
				String suffix = (colorMark ? "" : ChatColor.getLastColors(prefix));
				suffix = suffix + name.substring(prefix.length());
				if (suffix.length() <= 16) {
					team.setSuffix(suffix);
				} else {
					team.setSuffix(suffix.substring(0, 16));
				}
			} else {
				team.setSuffix("");
			}
		}
	}
}