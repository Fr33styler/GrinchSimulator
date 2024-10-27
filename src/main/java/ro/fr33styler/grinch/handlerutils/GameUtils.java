package ro.fr33styler.grinch.handlerutils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

public class GameUtils {
	
    public static List<Location> getDeserializedLocations(List<String> list, int yFix) {
        List<Location> loclist = new ArrayList<Location>();
        for (String l : list) {
        	loclist.add(getDeserializedLocation(l, yFix));
        }
        return loclist;
    }
    
    public static ItemFrame getFrame(Location loc) {
        for (Entity e : loc.getChunk().getEntities()) {
            if (e.getType() == EntityType.ITEM_FRAME) {
                if (e.getLocation().getBlock().getLocation().distance(loc) <= 1) {
                    return (ItemFrame) e;
                }
            }
        }
        return null;
    }
    
	public static void setValue(Class<?> c, String field, Object i, Object v) {
		try {
		    Field f = c.getDeclaredField(field);
		    f.setAccessible(true);
		    f.set(i, v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public static List<String> getSerializedLocations(List<Location> list) {
        List<String> loclist = new ArrayList<String>();
        for (Location l : list) {
        	loclist.add(getSerializedLocation(l));
        }
        return loclist;
    }
    
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

	public static boolean contains(Player[] top, Player p) {
    	for (Player pl : top) {
    		if (pl == p) {
    			return true;
    		}
    	}
		return false;
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