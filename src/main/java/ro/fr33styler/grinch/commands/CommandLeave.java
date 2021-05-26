package ro.fr33styler.grinch.commands;

import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.handler.Game;

public class CommandLeave implements Command {

	private Main main;

	public CommandLeave(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "leave";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public boolean hasPermission(Player p) {
		return true;
	}
	
	@Override
	public void executeCommand(Player p, String[] args) {
		Game g = main.getManager().getGame(p);
		if (g == null) {
			p.sendMessage(Messages.PREFIX + " " +Messages.GAME_NOGAME_LEAVE);
		} else {
			p.sendMessage(Messages.PREFIX + " " + Messages.GAME_LEFT);
			if (main.getManager().isBungeeMode()) {
				main.getManager().removePlayer(p, g, true, false);
			} else {
				main.getManager().removePlayer(p, g, false, false);
			}
		}
	}

}