package ro.fr33styler.grinch.commands;

import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.handler.Game;

public class CommandDelete implements Command {

	private Main main;
	
	public CommandDelete(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "delete";
	}

	@Override
	public String[] getArguments() {
		return new String[] { "<id>" };
	}

	@Override
	public boolean hasPermission(Player p) {
		return p.hasPermission("tl.admin");
	}

	@Override
	public void executeCommand(Player p, String[] args) {
		try {
			int id = Integer.parseInt(args[1]);
			Game g = main.getManager().getGame(id);
			if (g == null) {
				p.sendMessage(Messages.PREFIX + " §cNo game found with this ID.");
			} else {
				main.getManager().stopGame(g, false);
				main.getManager().removeGame(g);
				p.sendMessage(Messages.PREFIX + " §aGame was removed succesfuly.");
				main.getGameDatabase().set("Game." + id, null);
				main.getGameDatabase().save();
			}
		} catch (NumberFormatException e) {
			p.sendMessage(Messages.PREFIX + " §cMust be a number!");
		}
	}

}
