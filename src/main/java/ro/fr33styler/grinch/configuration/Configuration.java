package ro.fr33styler.grinch.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import ro.fr33styler.grinch.Main;

public class Configuration {

	private File file;
	private YamlConfiguration config;
	
	public Configuration(Main main, String name, boolean newFile) {
		file = new File(main.getDataFolder(), name);
		ConsoleCommandSender console = main.getServer().getConsoleSender();
		if (file.exists()) {
			console.sendMessage("§a - Loading "+name+"...");
		} else {
			console.sendMessage("§a - Creating a new "+name);
			if (newFile) {
			   try {
				  file.createNewFile();
			   } catch (IOException e) {
				  e.printStackTrace();
			   }
			} else {
			   main.saveResource(name, true);
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public ConfigurationSection getConfigurationSection(String configuration) {
		return config.getConfigurationSection(configuration);
	}
	
	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}
	
	public int getInt(String path) {
		return config.getInt(path);
	}
	
	public String getString(String path) {
		return config.getString(path);
	}
	
	public void set(String path, Object value) {
		config.set(path, value);
	}
	
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	public boolean isString(String path) {
		return config.isString(path);
	}
}