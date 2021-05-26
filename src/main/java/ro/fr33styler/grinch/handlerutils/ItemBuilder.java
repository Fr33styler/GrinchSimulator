package ro.fr33styler.grinch.handlerutils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
	
	public static ItemStack create(Material m, int number, String nume, String lore) {
		ItemStack is = new ItemStack(m, number);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(nume.replace("&", "§"));
		if (lore != null) {
		    im.setLore(Arrays.asList(lore.replace("&", "§").split("#")));
		}
		is.setItemMeta(im);
		    
	    return is;
	}
	
	public static ItemStack create(Material m, int number, byte data, String nume) {
		ItemStack is = new ItemStack(m, number, data);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(nume.replace("&", "§"));
		is.setItemMeta(im);
		    
	    return is;
	}
	
}