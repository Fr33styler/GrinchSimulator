package ro.fr33styler.grinch;

public enum Messages {
	
	PREFIX("§8▎ §3Grinch Simulator§8 ▏"),
	GAME_IS_FULL("§cGame is full!"),
	GAME_NULL("§cGame dosen't exist!"),
	GAME_HAS_STARTED("§aGame already started!"),
	GAME_JOIN_ANOTHER_GAME("§cYou can't play in more than 1 game!"),
	GAME_START("§7Game starts in §c%timer%§7 seconds."),
	GAME_JOIN("§a%name% §7has joined the game. §8(§d%size%§8/§d%maxsize%§8)"),
	GAME_LEAVE("§a%name% §7has left the game. §8(§d%size%§8/§d%maxsize%§8)"),
	GAME_LEFT("§cYou left the game"),
	GAME_NOGAME_LEAVE("§cYou have to be in game to use this command!"),
	GAME_NO_PLAYERS("§c§lNot enough player for game to continue!"),
	GAME_YOU_STOLE("§aYou stole a present!"),
	GAME_OVER("§cGame Over!"),
	GAME_WON("§aYOU WIN!"),
	GAME_NAME("§f§l   Grinch Simulator"),
	SIGN_FIRST("%prefix%"),
	SIGN_SECOND("Right click"),
	SIGN_THIRD("§5• §f§l%state% §5•"),
	SIGN_FOURTH("§c»§8§l%min%/%max%§c«"),
	STATE_WAITING("WAITING"),
	STATE_IN_GAME("IN GAME"),
	STATE_ENDING("ENDING"),
	TITLE_FIRST("§aYou got FIRST PLACE!"),
	TITLE_SECOND("§eYou got SECOND PLACE!"),
	TITLE_THIRD("§eYou got THIRD PLACE!"),
	TITLE_OVER("§eYou got %rank%th place!"),
	TITLE_REMAINING("§eseconds remaining"),
	NOT_ENOUGH_PLAYERS("§c§lNot enough players!"),
	ITEM_RIGHT_CLICK("Right click"),
	ITEM_LEFTGAME_NAME("§c§lLeave game"),
	ITEM_LEFTGAME_LORE("§7Leave the game."),
	BAR_PLAYERS("§cThere should be at least §b%min%§c for game to begin!"),
	RESTRICTED_COMMAND("§cYou can't use commands in-game!"), 
	SCOREBOARD_TITLE("§e§lGrinch Simulator"),
	SCOREBOARD_LOBBY_ID("Map: §a#"),
	SCOREBOARD_LOBBY_PLAYERS("Players:"),
	SCOREBOARD_LOBBY_GAME_START("Game starts in:"),
	SCOREBOARD_LOBBY_WAITING("Waiting..."),
	SCOREBOARD_LOBBY_SERVER("§ewww.spigotmc.org"),
	SCOREBOARD_GAME_RANKING("Ranking: §a"),
	SCOREBOARD_GAME_GIFTS_STOLEN("Gifts Stolen: §a"),
	SCOREBOARD_GAME_GIFTS_LEFT("Gifts Left: §a"),
	SCOREBOARD_GAME_TIME_LEFT("Time Left: §a"),
	SCOREBOARD_TOP("§c§l%place%. §f%player%§c: §a%gifts%"),
	GAME_TOP("§c§l%place%. §f%player%§c: §a%gifts%"),
	GAME_START_MESSAGE("§e§lGoal: Steal more presents than any#§e§lother player before the time runs out!");

	private String msg;
	
	Messages(String msg) {
       this.msg = msg;
	}
	
	public void setMessage(String msg) {
		this.msg = msg.replace('&', '§');
	}
	
	public String toString() {
		return msg;
	}
	
	public static Messages getEnum(String name) {
		for (Messages type : values()) {
			if (type.name().equals(name)) {
				return type;
			}
		}
		return null;
	}
}