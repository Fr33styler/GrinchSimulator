package ro.fr33styler.grinch.commands;

import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.handler.Game;

public class CommandQuickJoin implements Command {
	
	private Main main;
	
	public CommandQuickJoin(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "quickjoin";
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
		Game g = main.getManager().searchGame();
		if (g != null) {
			main.getManager().addPlayer(g, p);
		} else {
			p.sendMessage(Messages.PREFIX + " " + Messages.GAME_IS_FULL);
		}
	}

}
