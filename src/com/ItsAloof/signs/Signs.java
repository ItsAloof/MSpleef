package com.ItsAloof.signs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ItsAloof.HubSpleefPlugin;
import com.ItsAloof.API.APIClass;
import com.ItsAloof.utils.Utils;

public class Signs implements Listener {
	HubSpleefPlugin plugin;
	APIClass api;
	Utils utils;

	public Signs(HubSpleefPlugin plugin) {
		this.plugin = plugin;
		api = new APIClass(plugin);
		utils = new Utils(plugin);
	}

	@EventHandler
	public void onSign(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[MiniSpleef]")) {
			if (api.checkArena(event.getLine(1))) {
				if (plugin.signs.containsKey(api.getArena(event.getLine(1).toString()))) {
					plugin.signs.get(api.getArena(event.getLine(1).toString())).add(event.getBlock().getLocation());
					event.setLine(0, "§f[§bMiniSpleef§f]");
					event.getBlock().getState().update();
					return;
				} else {
					List<Location> temp = new ArrayList<Location>();
					temp.add(event.getBlock().getLocation());
					event.setLine(0, "§f[§bMiniSpleef§f]");
					event.getBlock().getState().update();
					plugin.signs.put(api.getArena(event.getLine(1)), temp);
					return;
				}
			} else {
				event.setLine(1, ChatColor.RED + event.getLine(1));
				event.getBlock().getState().update();
				return;
			}
		} else {
			return;
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (utils.isSign(event.getClickedBlock().getLocation())) {
				Sign s = (Sign) event.getClickedBlock().getState();
				if (s.getLine(0).toString().equalsIgnoreCase("§f[§bMiniSpleef§f]")) {
					if (api.checkArena(s.getLine(1).toString())) {
						if (plugin.signs.containsKey(api.getArena(s.getLine(1)))) {
							if (plugin.signs.get(api.getArena(s.getLine(1)))
									.contains(event.getClickedBlock().getLocation())) {
								Player player = event.getPlayer();
								sendPlayerList(api.getArena(s.getLine(1)), player);
								return;
							} else {
								plugin.signs.get(api.getArena(s.getLine(1))).add(event.getClickedBlock().getLocation());
								event.setCancelled(true);
								return;
							}
						} else if (!plugin.signs.containsKey(api.getArena(s.getLine(1)))) {
							List<Location> locs = new ArrayList<Location>();
							locs.add(event.getClickedBlock().getLocation());
							plugin.signs.put(api.getArena(s.getLine(1)), locs);
							event.setCancelled(true);
							return;
						} else {
							return;
						}
					}
				} else {
					return;
				}
			} else {
				return;
			}
		}
	}

	public void sendPlayerList(String arena, Player player) {
		int x = 1;
		player.sendMessage("§a--------------------");
		player.sendMessage("§ePlayers in " + arena);
		player.sendMessage("§a--------------------");
		if (plugin.enabled.containsKey(arena)) {
			for (Player p : plugin.enabled.get(arena)) {
				player.sendMessage("§6" + x + ". " + p.getName());
				x++;
			}
			return;
		} else {
			player.sendMessage("§cThere are no players in this arena!");
			return;
		}
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		if (plugin.enabled.isEmpty())
			return;
		for (String arena : plugin.enabled.keySet()) {
			if (plugin.enabled.get(arena).contains(event.getPlayer())) {
				plugin.enabled.get(arena).remove(event.getPlayer());
				event.getPlayer().teleport(plugin.arenas.get(arena).get(2));
				return;
			} else {
				continue;
			}
		}
		return;
	}
}
