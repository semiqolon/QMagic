package com.github.semiqolon.QMagic;

import java.util.*;
import java.sql.Timestamp;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Location;

public class QMagic extends JavaPlugin {
	int creationspellrange = 30;
	int defaultcooldown = 5;
	Date fakedate = new Date();
	Map<String, Date> cooldowns = new HashMap<String, Date>();
	ChatColor failurecolor = ChatColor.GRAY;
	ChatColor successcolor = ChatColor.AQUA;
	Logger log;
	
	
	public void onEnable() {
		log = this.getLogger();
		log.info("QMagic Enabled.");
						
	}
	
	public void onDisable() {
		log = this.getLogger();
		log.info("QMagic Disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equals("c") || cmd.getName().equals("cast")) {
			
			Player caster = (Player) sender;
			String spellname;
			String permissionname;
			Date cooldowndate = new Date();
			Date castdate = cooldowndate;
			
			if (args.length == 0) {
				sender.sendMessage(failurecolor + "You must supply a spell name.");
				return true;
			} else {
				spellname = args[0];
				permissionname = "qmagic." + spellname;
			}
			
			if (this.cooldowns.containsKey(caster.getName())) {
				cooldowndate = cooldowns.get(caster.getName());
				long cooldowntime = cooldowndate.getTime();
				long casttime = castdate.getTime();
				long timeleft = cooldowntime - casttime;
				if (timeleft > 0) {
					long secondsleft = (timeleft / 1000) + 1;
					String plural = "s";
					if (secondsleft == 1) {
						plural = "";
					}
					failedspell(caster, "Your magical energies must recharge for " + secondsleft + " more second" + plural + ".");
					return true;
				}
			} 
			
			
			if (!caster.hasPermission(permissionname)) {			
				String message = "You do not know a spell by that name.";
				caster.sendMessage(failurecolor + message);
				return true;				
			}
			
			if (spellname.equals("createwater")) {				
				creationspell(Material.WATER, caster);
				setcooldown(caster, 5);
				return true;
			}
			
			if (spellname.equals("createlava")) {
				creationspell(Material.LAVA, caster);
				setcooldown(caster, 10);
				return true;
			}
			
		}
		
		return true;
	}
	
	private void setcooldown(Player caster, int cooldowntime) {
		if (cooldowns.containsKey(caster.getName())) {
			cooldowns.remove(caster.getName());
		}
		Date currenttime = new Date();
		long currentms = currenttime.getTime();
		currentms += cooldowntime * 1000;
		currenttime.setTime(currentms);
		cooldowns.put(caster.getName(), currenttime);
		
	}
	
	private void failedspell(Player caster, String message) {
		caster.sendMessage(failurecolor + message);
	}
	
	private void creationspell(Material material, Player originator) {
		List< Block > los = originator.getLastTwoTargetBlocks(null, creationspellrange);
		
		Block targetblock = los.get(1);
		if (targetblock.getType() != Material.AIR) {
			Block creationblock = los.get(0);
			creationblock.setType(material);
		} else {
			failedspell(originator, "Out of range.");
		}
		
	}
	
}