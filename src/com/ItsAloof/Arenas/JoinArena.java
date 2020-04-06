package com.ItsAloof.Arenas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ItsAloof.HubSpleefPlugin;
import com.ItsAloof.utils.Utils;

public class JoinArena {
	HubSpleefPlugin plugin;
	Utils utils;

	public JoinArena(HubSpleefPlugin plugin) {
		this.plugin = plugin;
		this.utils = new Utils(plugin);
	}

	public void saveInv(Player player) {
		utils.saveInventory(player);
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				player.getInventory().clear();
			}
		};
		br.runTaskLater(plugin, 10L);
		return;
	}

	public void startChecker() {
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				if (plugin.arenas.isEmpty()) {
					return;
				} else {
					for (Player player : Bukkit.getOnlinePlayers()) {
						for (String arena : plugin.arenas.keySet()) {
							if (addPlayer(arena, player)) {
								player.setFlying(false);
								player.setHealth(20);
								player.setFoodLevel(20);
								player.setSaturation(10);
								saveInv(player);
							}
						}
					}
				}
			}
		};
		br.runTaskTimer(plugin, 5L, 10L);
	}

	public boolean addPlayer(String arena, Player player) {
		if (plugin.isInRect(player, plugin.getLoc1(arena), plugin.getLoc2(arena))) {
			if (plugin.enabled.isEmpty()) {
				List<Player> temp = new ArrayList<Player>();
				temp.add(player);
				plugin.enabled.put(arena, temp);
				player.sendMessage("§aJoined arena §6§n" + arena);
				return true;
			} else {
				if (plugin.enabled.containsKey(arena)) {
					if (!plugin.enabled.get(arena).contains(player) && !plugin.enabledArenas.contains(arena)) {
						List<Player> temp = plugin.enabled.get(arena);
						temp.add(player);
						player.setFlying(false);
						plugin.enabled.put(arena, temp);
						saveInv(player);
						player.sendMessage("§aJoined arena §6§n" + arena);
						plugin.ArenaJoin(player, arena, "§6" + player.getName() + " §ahas joined the arena!");
						if (plugin.canStart(arena)) {
							if (!plugin.queued.contains(arena)) {
								plugin.startGame(arena);
								plugin.queued.add(arena);
								return true;
							} else if (plugin.enabledArenas.contains(arena)) {
								player.sendMessage(
										plugin.prefix + " §cYou cannot join a game that has already started!");
								player.teleport(plugin.enabled.get(arena).get(2));
								plugin.enabled.get(arena).remove(player);
								return false;
							} else if (plugin.getMaxPlayers(arena) <= plugin.enabled.get(arena).size()) {
								player.sendMessage(plugin.prefix
										+ " §cYou cannot join this game is full wait until the next round!");
								player.teleport(plugin.enabled.get(arena).get(2));
							} else {
								return false;
							}
						}
					}
				} else {
					if (plugin.checkFull(arena) == false) {
						List<Player> temp = new ArrayList<Player>();
						temp.add(player);
						player.setFlying(false);
						plugin.enabled.put(arena, temp);
						player.sendMessage("§aJoined arena §6§n" + arena);
						plugin.ArenaJoin(player, arena, "§6" + player.getName() + " §ahas joined the arena!");
						if (plugin.canStart(arena)) {
							plugin.startGame(arena);
						}
						return true;
					}
				}
			}
		} else if (plugin.enabled.get(arena) == null || plugin.enabled.isEmpty()) {
			return false;
		} else if (plugin.enabled.get(arena).contains(player) && !plugin.enabledArenas.contains(arena)) {
			utils.setInventory(player);
			player.sendMessage(plugin.prefix + " §cYou have left §6§n" + arena);
			plugin.enabled.get(arena).remove(player);
			if (plugin.enabled.get(arena).isEmpty()) {
				plugin.enabled.remove(arena);
				return false;
			}
		} else {
			return false;
		}
		return false;
	}

	public boolean updateCode(Player player, String arena) {
		Utils utils = new Utils(plugin);
		if (utils.isInArena(player.getLocation())) {
			if (plugin.enabled.isEmpty() || !plugin.enabled.containsKey(arena)) {
				List<Player> players = new ArrayList<Player>();
				players.add(player);
				plugin.enabled.put(arena, players);
				return true;
			}

		} else if (plugin.enabled.containsKey(arena) && plugin.enabled.get(arena).contains(player)) {

		} else {
			return false;
		}
		return false;
	}
}
