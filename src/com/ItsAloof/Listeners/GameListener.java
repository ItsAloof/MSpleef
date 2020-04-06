package com.ItsAloof.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.ItsAloof.HubSpleefPlugin;
import com.ItsAloof.utils.Utils;

public class GameListener implements Listener {
	HubSpleefPlugin plugin;
	Utils utils;

	public GameListener(HubSpleefPlugin plugin) {
		this.plugin = plugin;
		this.utils = new Utils(plugin);
	}

	@EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
		for (String arena : plugin.arenas.keySet()) {
			if (plugin.enabled.containsKey(arena)) {
				if (plugin.enabled.get(arena).contains(e.getPlayer())) {
					e.setCancelled(true);
					e.getPlayer().setFlying(false);
					continue;
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		return;
	}
	

	@EventHandler(priority = EventPriority.HIGH)
	public void onFall(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (utils.isInArena(player.getLocation())) {
				e.setCancelled(true);
				return;
			} else if (plugin.noFall.contains(player)) {
				e.setCancelled(true);
				plugin.noFall.remove(player);
				return;
			} else {
				return;
			}
		}
		return;
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (plugin.disableCommands(e.getPlayer())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cCommands are disabled while in in arena!");
			return;
		} else {
			return;
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (utils.isInArena(event.getBlock().getLocation()) && !plugin.edit.contains(event.getPlayer())) {
			event.setCancelled(true);
			return;
		} else {
			return;
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (utils.isInArena(event.getWhoClicked().getLocation())) {
			event.setCancelled(true);
			event.getWhoClicked().closeInventory();
			return;
		} else {
			return;
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (utils.isInArena(event.getPlayer().getLocation())) {
			event.setCancelled(true);
			return;
		} else {
			return;
		}
	}

}
