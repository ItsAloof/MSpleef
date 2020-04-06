package com.ItsAloof.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.ItsAloof.HubSpleefPlugin;
import com.ItsAloof.utils.Utils;

public class BreakBlock implements Listener {

	HubSpleefPlugin plugin;
	Utils utils;

	public BreakBlock(HubSpleefPlugin plugin) {
		this.plugin = plugin;
		this.utils = new Utils(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent event) {
		if (!plugin.edit.contains(event.getPlayer())) {
			Player player = event.getPlayer();
			event.setCancelled(canBreakBlock(event.getBlock().getLocation(), player));
			return;
		}
	}

	public void addBlock(String arena, Location l) {
		List<Location> temp = new ArrayList<Location>();
		if (plugin.blocks.containsKey(arena)) {
			plugin.blocks.get(arena).add(l);
			l.getBlock().setType(Material.AIR);
			return;
		} else {
			temp.add(l);
			plugin.blocks.put(arena, temp);
			l.getBlock().setType(Material.AIR);
			return;
		}
	}

	public boolean isInRect(Location location, Location loc1, Location loc2) {
		double[] dim = new double[2];

		dim[0] = loc1.getX();
		dim[1] = loc2.getX();
		Arrays.sort(dim);
		if (location.getX() > dim[1] || location.getX() < dim[0])
			return false;
		dim[0] = loc1.getY();
		dim[1] = loc2.getY();
		Arrays.sort(dim);
		if (location.getY() > dim[1] || location.getY() < dim[0])
			return false;

		dim[0] = loc1.getZ();
		dim[1] = loc2.getZ();
		Arrays.sort(dim);
		if (location.getZ() > dim[1] || location.getZ() < dim[0])
			return false;

		return true;
	}

	public boolean canBreakBlock(Location blockLoc, Player player) {
		if (isInGame(player) && blockLoc.getBlock().getType().equals(Material.SNOW_BLOCK)) {
			addBlock(utils.getPlayersArena(player), blockLoc);
			return true;
		} else if (utils.getPlayersArena(player) == null
				&& !plugin.enabledArenas.contains(utils.getPlayersArena(player)) && isInArea(blockLoc)) {
			return true;
		} else if (utils.isInArena(player.getLocation()) && isInArea(blockLoc)) {
			return true;
		} else if (utils.getPlayersArena(player) != null && !isInArea(blockLoc)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isInArea(Location l) {
		for (String arena : plugin.arenas.keySet()) {
			for (Location loc : utils.genCube(utils.getLoc1(arena), utils.getLoc2(arena))) {
				if (loc.equals(l)) {
					return true;
				} else {
					continue;
				}
			}
			continue;
		}
		return false;
	}

	public boolean isInGame(Player player) {
		if (utils.getPlayersArena(player) == null) {
			return false;
		} else if (plugin.enabledArenas.contains(utils.getPlayersArena(player))) {
			return true;
		} else {
			return false;
		}
	}

}
