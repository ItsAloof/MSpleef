package com.ItsAloof.Arenas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ItsAloof.HubSpleefPlugin;
import com.ItsAloof.utils.Utils;

public class ArenaBuilder {
	HubSpleefPlugin plugin;
	Utils utils;
	public String name;
	public Location l1;
	public Location l2;
	public Location spawn;
	public int minPlayers;
	public int maxPlayers;

	public ArenaBuilder(HubSpleefPlugin plugin) {
		this.plugin = plugin;
		this.utils = new Utils(plugin);
	}

	public void buildArena(Player builder) {
		this.l1 = plugin.l1.get(builder);
		this.l2 = plugin.l2.get(builder);
		if (l1 != null && l2 != null) {
			List<Location> locs = new ArrayList<Location>();
			List<String> locations = new ArrayList<String>();
			locations.add("l1 " + convertToString(l1));
			locations.add("l2 " + convertToString(l2));
			locations.add("spawn " + convertToString(spawn));
			locs.add(l1);
			locs.add(l2);
			locs.add(spawn);
			plugin.arenas.put(name, locs);
			plugin.getConfig().set("Arenas." + name, name);
			plugin.getConfig().set("Arenas." + name + ".Locations", locations);
			plugin.getConfig().set("Arenas." + name + ".maxPlayers", maxPlayers);
			plugin.getConfig().set("Arenas." + name + ".minPlayers", minPlayers);
			plugin.saveConfig();
			plugin.reloadConfig();
			builder.sendMessage(
					plugin.getLanguage().getString("Create-Arena").replace("{arena}", name).replace("&", "§"));
			return;
		} else {
			builder.sendMessage("§4Error: §cMake sure you have selected 2 locations!");
			return;
		}
	}

	@SuppressWarnings("deprecation")
	public void setDoor(String arena, Location l1, Location l2) {
		HashMap<Location, String> locations = new HashMap<Location, String>();
		for (Location l : utils.genCube(l1, l2)) {
			ItemStack item = new ItemStack(l.getBlock().getType(), 1, l.getBlock().getData());
			locations.put(l, item.getData().toString());
			l.getWorld().playEffect(l, Effect.STEP_SOUND, l.getBlock().getType());
			l.getBlock().setType(Material.AIR);
			continue;
		}
		List<String> ii = new ArrayList<String>();
		for (Location locs : locations.keySet()) {
			String x = "x:" + locs.getBlockX() + " y:" + locs.getBlockY() + " z:" + locs.getBlockZ() + " world:"
					+ locs.getWorld().getName() + " " + locations.get(locs);
			ii.add(x);
		}
		plugin.getConfig().set("Arenas." + arena + ".Entrances", ii);
		plugin.saveConfig();
		plugin.reloadConfig();
		return;
	}

	public Location getSpawn() {
		return this.spawn;
	}

	public Location getLoc1() {
		return this.l1;
	}

	public Location getLoc2() {
		return this.l2;
	}

	public String getName() {
		return this.name;
	}

	public int getMaxPlayer() {
		return this.maxPlayers;
	}

	public int getMinPlayer() {
		return this.minPlayers;
	}

	public String convertToString(Location l) {
		return new String("x:" + l.getBlockX() + " y:" + l.getBlockY() + " z:" + l.getBlockZ() + " world:"
				+ l.getWorld().getName());
	}

}