package ro.fr33styler.grinch.handlerutils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class GameUtils {
    
    public static List<String> getSerializedBlocks(List<Block> list) {
        List<String> loclist = new ArrayList<String>();
        for (Block b : list) {
        	loclist.add(getSerializedLocation(b.getLocation()));
        }
        return loclist;
    }
    
    public static List<Block> getDeserializedBlocks(List<String> list) {
        List<Block> loclist = new ArrayList<Block>();
        for (String b : list) {
        	loclist.add(getDeserializedLocation(b, 0).getBlock());
        }
        return loclist;
    }
    
    public static String getSerializedLocation(Location l) {
        return l.getWorld().getName() + "," + (l.getBlockX()+0.5) + "," + l.getBlockY() + "," + (l.getBlockZ()+0.5) + "," + l.getYaw() + "," + l.getPitch();
    }
    
    public static Location getDeserializedLocation(String s, int yFix) {
    	String[] st = s.split(",");
    	return new Location(Bukkit.getWorld(st[0]), Double.parseDouble(st[1]), Double.parseDouble(st[2])+yFix, Double.parseDouble(st[3]), Float.parseFloat(st[4]), Float.parseFloat(st[5]));
    }

	public static boolean containsIgnoreCase(List<String> list, String cmd) {
		for (String value : list) {
			if (cmd.equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
	}

}