package com.ItsAloof.API;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.ItsAloof.HubSpleefPlugin;

public class APIClass {
	HubSpleefPlugin plugin;

	public APIClass(HubSpleefPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean isInGame(Player player) {
		if (plugin.enabled.isEmpty()) {
			return false;
		} else {
			for (String arena : plugin.arenas.keySet()) {
				for (Player p : plugin.enabled.get(arena)) {
					if (p == player) {
						return true;
					} else {
						continue;
					}
				}
			}
		}
		return false;
	}

	public List<String> getPlayers(String arena) {
		List<String> temp = new ArrayList<String>();
		if (plugin.enabled.containsKey(arena)) {
			for (Player player : plugin.enabled.get(arena)) {
				temp.add(player.getName());
			}
			return temp;
		} else {
			return null;
		}
	}

	public boolean checkArena(String arena) {
		for (String arenas : plugin.arenas.keySet()) {
			if (arenas.equalsIgnoreCase(arena)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	public String getArena(String arena) {
		for (String arenas : plugin.arenas.keySet()) {
			if (arenas.equalsIgnoreCase(arena)) {
				return arenas;
			} else {
				continue;
			}
		}
		return null;
	}

	public String getPlayersArena(Player player) {
		for (String arena : plugin.enabled.keySet()) {
			for (Player p : plugin.enabled.get(arena)) {
				if (p == player) {
					return arena;
				} else {
					continue;
				}
			}
		}
		return null;
	}

	public int getPlayerCount(String arena) {
		if (plugin.enabled.get(arena) == null) {
			return 0;
		} else {
			return plugin.enabled.get(arena).size();
		}
	}

	public List<Player> getPlayersInGame() {
		List<Player> players = new ArrayList<Player>();
		for (String arenas : plugin.enabled.keySet()) {
			for (Player player : plugin.enabled.get(arenas)) {
				players.add(player);
			}
		}
		return players;
	}

	public List<String> getArenas() {
		List<String> arenas = new ArrayList<String>();
		for (String arena : plugin.arenas.keySet()) {
			arenas.add(arena);
		}
		return arenas;
	}

}
