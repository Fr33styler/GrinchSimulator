package ro.fr33styler.grinch.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.cache.PlayerStats;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Main main;

    public PlaceholderAPIHook(Main main) {
        this.main = main;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "Fr33styler";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "grinch";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String identifier) {
        PlayerStats stats = main.getStatistics().get(p.getUniqueId());
        if (stats == null) return null;

        if (identifier.equals("wins")) {
            return String.valueOf(stats.getWins());
        }
        if (identifier.equals("gifts_stolen")) {
            return String.valueOf(stats.getGiftsStolen());
        }
        return null;
    }
}
