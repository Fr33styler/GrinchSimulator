package ro.fr33styler.grinch.cache;

import java.util.Collection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerData {

	private final float xp;
	private final int food;
	private final int level;
	private final Player p;
	private final Location loc;
	private final GameMode mode;
	private final double health;
	private final double maxhealth;
	private final int fireticks;
	private final float flyspeed;
	private final float walkspeed;
	private final boolean isFlying;
	private final float fallDistance;
	private final ItemStack[] armour;
	private final ItemStack[] inventory;
	private final Collection<PotionEffect> effects;

	public PlayerData(Player p) {
		this.p = p;
		xp = p.getExp();
		level = p.getLevel();
		loc = p.getLocation();
		health = p.getHealth();
		mode = p.getGameMode();
		food = p.getFoodLevel();
		isFlying = p.getAllowFlight();
		flyspeed = p.getFlySpeed();
		fireticks = p.getFireTicks();
		walkspeed = p.getWalkSpeed();
		maxhealth = p.getMaxHealth();
		fallDistance = p.getFallDistance();
		effects = p.getActivePotionEffects();
		inventory = p.getInventory().getContents();
		armour = p.getInventory().getArmorContents();
		reset();
	}

	public void reset() {
		p.setExp(0);
		p.setLevel(0);
		p.setFireTicks(0);
		p.setFoodLevel(20);
		p.setFlying(false);
		p.setFlySpeed(0.2F);
		p.setWalkSpeed(0.2F);
		p.setFallDistance(0);
		p.setAllowFlight(false);
		p.getInventory().clear();
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setGameMode(GameMode.SURVIVAL);
		p.getInventory().setArmorContents(null);
		if (p.isInsideVehicle()) {
			p.leaveVehicle();
		}
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		p.closeInventory();
	}

	public void restore(boolean teleport) {
		if (teleport) {
		   p.teleport(loc);
		}
		p.setExp(xp);
		p.setLevel(level);
		p.setGameMode(mode);
		p.setMaxHealth(maxhealth);
		p.setHealth(health);
		p.setFoodLevel(food);
		if (isFlying) {
			p.setAllowFlight(true);
			p.setFlying(true);
		} else {
			p.setAllowFlight(true);
			p.setFlying(false);
			p.setAllowFlight(false);
		}
		p.setFlySpeed(flyspeed);
		p.setFireTicks(fireticks);
		p.setWalkSpeed(walkspeed);
		p.setFallDistance(fallDistance);
		p.getInventory().setArmorContents(armour);
		p.getInventory().setContents(inventory);
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		for (PotionEffect effect : effects) {
			p.addPotionEffect(effect);
		}
		p.updateInventory();
	}
}