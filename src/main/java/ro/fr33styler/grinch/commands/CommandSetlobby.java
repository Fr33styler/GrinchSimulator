package ro.fr33styler.grinch.commands;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.handler.GameSetup;

public class CommandSetlobby implements Command {

	private Main main;

	public CommandSetlobby(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "setlobby";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public boolean hasPermission(Player p) {
		GameSetup setup = main.getSetups().get(p);
		return setup != null && setup.getStep() == 0;
	}

	@Override
	public void executeCommand(Player p, String[] args) {
		GameSetup setup = main.getSetups().get(p);
		setup.nextStep();
		setup.setLobby(p.getLocation().clone());
		for (int i = 0; i < 20; i++) {
			p.sendMessage("");
		}
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
		p.sendMessage(Messages.PREFIX + " §7The lobby was succesfully set. Now right click using a");
		p.sendMessage(Messages.PREFIX + " §cDIAMON SWORD §7to add gifts. When you've done type /gs finish!");
	}

}
