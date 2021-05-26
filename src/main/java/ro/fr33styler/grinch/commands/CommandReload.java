package ro.fr33styler.grinch.commands;

import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;

public class CommandReload implements Command {

	private Main main;
	
	public CommandReload(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "reload";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public boolean hasPermission(Player p) {
		return p.hasPermission("gs.admin");
	}

	@Override
	public void executeCommand(Player p, String[] args) {
        main.getServer().getPluginManager().disablePlugin(main);
        main.getServer().getPluginManager().enablePlugin(main);
        p.sendMessage(Messages.PREFIX + " §aGrinch Simulator has been succesfuly reloaded!");
	}

}
