package ro.fr33styler.grinch.commands;

import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;

public class CommandJoin implements Command {

	private Main main;

	public CommandJoin(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "join";
	}

	@Override
	public String[] getArguments() {
		return new String[] { "<id>" };
	}
	
	@Override
	public boolean hasPermission(Player p) {
		return true;
	}
	
	@Override
	public void executeCommand(Player p, String[] args) {
		try {
			main.getManager().addPlayer(main.getManager().getGame(Integer.parseInt(args[1])), p);
		} catch (NumberFormatException e) {
			p.sendMessage(Messages.PREFIX + "§cMust be a number!");
		}
	}

}