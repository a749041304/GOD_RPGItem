package org.jason.rpgitem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Items {
	
	private static File f;
	
	private static FileConfiguration data;
	
	private static Main plugin;
	
	static {
		plugin = Main.getInstance();
		f = new File (plugin.getDataFolder(),"items.yml");
		data = YamlConfiguration.loadConfiguration(f);
	}
	
	public static void add (String flag, ItemStack item) {
		data.set(flag, item);
		save ();
	}
	
	public static ItemStack get (String flag) {
		return data.getItemStack(flag);
	}
	
	public static List <ItemStack> getAll () {
		List <ItemStack> items = new ArrayList <ItemStack> ();
		for (String s : data.getKeys(false)) {
			ItemStack item = data.getItemStack(s);
			items.add(item);
		}
		return items;
	}
	
	private static void save () {
		try {
			data.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
