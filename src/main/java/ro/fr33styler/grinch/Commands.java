package ro.fr33styler.grinch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ro.fr33styler.grinch.commands.Command;
import ro.fr33styler.grinch.commands.CommandCreate;
import ro.fr33styler.grinch.commands.CommandDelete;
import ro.fr33styler.grinch.commands.CommandFinish;
import ro.fr33styler.grinch.commands.CommandHelp;
import ro.fr33styler.grinch.commands.CommandJoin;
import ro.fr33styler.grinch.commands.CommandLeave;
import ro.fr33styler.grinch.commands.CommandQuickJoin;
import ro.fr33styler.grinch.commands.CommandReload;
import ro.fr33styler.grinch.commands.CommandSetlobby;

public class Commands implements CommandExecutor {

	private List<Command> commands = new ArrayList<Command>();
	
	public Commands(Main main) {
		commands.add(new CommandHelp());
		commands.add(new CommandJoin(main));
		commands.add(new CommandLeave(main));
		commands.add(new CommandReload(main));
		commands.add(new CommandDelete(main));
		commands.add(new CommandCreate(main));
		commands.add(new CommandSetlobby(main));
		commands.add(new CommandFinish(main));
		commands.add(new CommandQuickJoin(main));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Messages.PREFIX + " §7Plugin made by §aFr33styler§7.");
			sender.sendMessage(Messages.PREFIX + " §7Type §c/gs help§7 for help.");		
		} else {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Messages.PREFIX + " §cYou can't use commands from console!");	
				return false;
			}
			Player p = (Player) sender;
			for (Command command : commands) {
				if (args.length >= 1 && args[0].equalsIgnoreCase(command.getCommand())) {
					if (args.length == command.getArguments().length+1) {
						if (command.hasPermission(p)) {
							command.executeCommand(p, args);
						} else break;
					} else {
						String arguments = "";
						for (String argument : command.getArguments()) {
							arguments = arguments + " " + argument;
						}
						p.sendMessage(Messages.PREFIX + " §7Invalid arguments! Use §a/gs " + command.getCommand() + arguments);
					}
					return true;
				}
			}
			sender.sendMessage(Messages.PREFIX + " §7Unknown command! Type §c/gs help§7 for help.");
		}
		return false;
	}

}
