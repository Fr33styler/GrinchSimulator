package ro.fr33styler.grinch.commands;

import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Messages;

public class CommandHelp implements Command {

	@Override
	public String getCommand() {
		return "help";
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
		p.sendMessage(Messages.PREFIX + " §7- §dHelp");
		p.sendMessage("- §7/§cgs join §8<§aid§8>");
		p.sendMessage("- §7/§cgs leave");
		if (p.hasPermission("tl.admin")) {
			p.sendMessage("- §7/§cgs reload");
			p.sendMessage("- §7/§cgs delete §8<§aid§8>");
			p.sendMessage("- §7/§cgs create §8<§aid§8> §8<§amin_players§8> §8<§amax_players§8>");
		}
	}
	
}
