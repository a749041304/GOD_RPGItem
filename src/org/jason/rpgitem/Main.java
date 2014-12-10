package org.jason.rpgitem;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public FileConfiguration config;
	
	private Random r = new Random ();
	
	private static Main instance;
	
	public static Main getInstance () {
		return instance;
	}
	
	public void onLoad() {
		instance = this;
		saveDefaultConfig();
		this.config = getConfig();
		System.out.println("[" + getName() + "]" + " " + getName() + "加载成功");
	}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			
			@Override
			public void run() {
				for (Player p : getServer().getOnlinePlayers()) {
					checkHealth(p);
				}
			}
		}, 0, 1 * 20);
		System.out.println("[" + getName() + "]" + " " + getName() + "开启成功");
	}

	public void onDisable() {
		HandlerList.unregisterAll((Plugin)this);
		getServer().getScheduler().cancelTasks(this);
		System.out.println("[" + getName() + "]" + " " + getName() + "关闭成功");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String cmd = command.getName();
		Player p = (Player) sender;
		if (cmd.equalsIgnoreCase("item")) {
			if (args[0].equalsIgnoreCase("save")) {
				if (args.length == 2) {
					Items.add(args[1], p.getItemInHand());
					p.sendMessage("添加成功");
				}
			}
			if (args[0].equalsIgnoreCase("get")) {
				if (args.length == 2) {
					p.getInventory().addItem(Items.get(args[1]));
					p.sendMessage("发送成功");
				}
			}
			return true;
		}
		return false;
	}
	
	//攻击
	@EventHandler
	public void onEntityDamageByPlayer (EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			ItemStack item = p.getItemInHand();
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLore()) {
						for (String s : item.getItemMeta().getLore()) {
							Pattern pattren = Pattern.compile("\\S*伤害:[ ](\\S+)");
							Matcher matcher = pattren.matcher(s);
							if (matcher.find()) {
								int damage = getValue(matcher.group(1));
								e.setDamage(damage);
							}
						}
					}
				}
			}
		}
	}
	
	//防御
	@EventHandler
	public void onPlayerDamageByEntity (EntityDamageByEntityEvent e) {
		if (((e.getDamager() instanceof LivingEntity) || (e.getDamager() instanceof Arrow)) && e.getEntity() instanceof Player) {
			int armor = 0;
			Player p = (Player) e.getEntity();
			for (ItemStack item : p.getEquipment().getArmorContents()) {
				if (item != null) {
					if (item.hasItemMeta()) {
						if (item.getItemMeta().hasLore()) {
							for (String s : item.getItemMeta().getLore()) {
								Pattern pattren = Pattern.compile("\\S*防御:[ ](\\S+)");
								Matcher matcher = pattren.matcher(s);
								if (matcher.find()) {
									armor += getValue(matcher.group(1));
								}
							}
						}
					}
				}
			}
			double damage = e.getDamage() - armor;
			if (damage < 0) damage = 2;
			e.setDamage(damage);
		}
	}
	
	public void checkHealth (Player p) {
		int health = org.jason.level.Main.instance.getHealth(p);
		for (ItemStack item : p.getEquipment().getArmorContents()) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLore()) {
						for (String s : item.getItemMeta().getLore()) {
							Pattern pattren = Pattern.compile("\\S*生命:[ ](\\S+)");
							Matcher matcher = pattren.matcher(s);
							if (matcher.find()) {
								health += getValue(matcher.group(1));
							}
						}
					}
				}
			}
		}
		try {
			p.setMaxHealth(health);
		} catch (Exception e) {
		}
	}
	
	public int getValue (String s) {
		int value = 0;
		if (s.split("[-]").length == 2) {
			int min = Integer.parseInt(s.split("-")[0]);
			int max = Integer.parseInt(s.split("-")[1]);
			value = this.r.nextInt(max)%(max-min+1) + min;
		} else {
			value = Integer.parseInt(s);
		}
		return value;
	}
	
	public int getValue (int min, int max) {
		return this.r.nextInt(max)%(max-min+1) + min;
	}
	
}