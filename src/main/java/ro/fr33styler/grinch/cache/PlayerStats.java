package ro.fr33styler.grinch.cache;

import org.bukkit.entity.Player;

public class PlayerStats {

    private int wins;
    private int giftsStolen;
    private final Player player;

    public PlayerStats(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getGiftsStolen() {
        return giftsStolen;
    }

    public void setGiftsStolen(int giftsStolen) {
        this.giftsStolen = giftsStolen;
    }
}
