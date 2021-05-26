package ro.fr33styler.grinch.commands;

import org.bukkit.entity.Player;

public interface Command {

	public String getCommand();
	
	public String[] getArguments();
	
	public boolean hasPermission(Player p);
	
	public void executeCommand(Player p, String[] args);
	
}