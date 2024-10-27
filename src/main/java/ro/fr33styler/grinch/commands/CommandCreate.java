package ro.fr33styler.grinch.commands;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.handler.GameSetup;

public class CommandCreate implements Command {

	private Main main;

	public CommandCreate(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "create";
	}

	@Override
	public String[] getArguments() {
		return new String[] { "<id>", "<min_players>", "<max_players>" };
	}
	
	@Override
	public boolean hasPermission(Player p) {
		return p.hasPermission("gs.admin");
	}
	
	@Override
	public void executeCommand(Player p, String[] args) {
		try {
			int id = Integer.parseInt(args[1]);
			int min = Integer.parseInt(args[2]);
			int max = Integer.parseInt(args[3]);
			if (min < 2) {
				p.sendMessage(Messages.PREFIX + " §cThe minimum should be bigger than 1");
				return;
			}
			for (GameSetup setup : main.getSetups().values()) {
				if (setup.getID() == id) {
					p.sendMessage(Messages.PREFIX + " §cA setup with same ID is already being made.");
					return;
				}
			}
			if (main.getManager().getGame(id) == null) {
				if (main.getSetups().get(p) == null) {
					main.getSetups().put(p, new GameSetup(id, min, max));
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
					for (int i = 0; i < 20; i++) {
						p.sendMessage("");
					}
					p.sendMessage(Messages.PREFIX + " §7Welcome to Grinch Simulator wizard setup. Please follow the commands!");
					p.sendMessage(Messages.PREFIX + " §7Use §c/gs setlobby§7 to set where players spawn.");
				} else {
					p.sendMessage(Messages.PREFIX + " §cYou must finish your previous setup first.");
				}
			} else {
				p.sendMessage(Messages.PREFIX + " §cA game with same ID already exists.");
			}
		} catch (NumberFormatException e) {
			p.sendMessage(Messages.PREFIX + " §cMust be a number!");
		}
	}

}