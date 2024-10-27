package ro.fr33styler.grinch.commands;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.Messages;
import ro.fr33styler.grinch.handler.Game;
import ro.fr33styler.grinch.handler.GameSetup;
import ro.fr33styler.grinch.handlerutils.GameUtils;

public class CommandFinish implements Command {

	private Main main;

	public CommandFinish(Main main) {
		this.main = main;
	}
	
	@Override
	public String getCommand() {
		return "finish";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public boolean hasPermission(Player p) {
		GameSetup setup = main.getSetups().get(p);
		return setup != null && setup.getStep() == 1 && setup.getGifts().size() > 0;
	}

	@Override
	public void executeCommand(Player p, String[] args) {
		GameSetup setup = main.getSetups().get(p);
		for (int i = 0; i < 20; i++) {
			p.sendMessage("");
		}
		setup.getLobby().getWorld().setWeatherDuration(0);
		main.getManager().addGame(new Game(main, setup.getID(), setup.getLobby(), setup.getMin(), setup.getMax(), setup.getGifts()));
		main.getGameDatabase().set("Game." + setup.getID() + ".Min", setup.getMin());
		main.getGameDatabase().set("Game." + setup.getID() + ".Max", setup.getMax());
		main.getGameDatabase().set("Game." + setup.getID() + ".Lobby", GameUtils.getSerializedLocation(setup.getLobby()));
		main.getGameDatabase().set("Game." + setup.getID() + ".Gifts", GameUtils.getSerializedBlocks(setup.getGifts()));
		main.getGameDatabase().save();
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
		p.sendMessage(Messages.PREFIX + " §aThe game has been successfully set.");
		main.getSetups().remove(p);
	}

}
