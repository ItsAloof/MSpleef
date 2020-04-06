package com.ItsAloof.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ItsAloof.HubSpleefPlugin;

public class LocationListener implements Listener {

	HubSpleefPlugin plugin;

	public LocationListener(HubSpleefPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onSelect(PlayerInteractEvent event) {
		ItemStack item = event.getPlayer().getItemInHand();
		Player player = event.getPlayer();

		if (player.hasPermission("minispleef.select")) {
			if (item == null || item.getType() != Material.GOLD_HOE) {
				return;
			} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				Location l = event.getClickedBlock().getLocation();
				plugin.l1.put(event.getPlayer(), event.getClickedBlock().getLocation());
				event.setCancelled(true);
				event.getPlayer().sendMessage(
						"§aLocation 2 set to (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ")");
				return;
			} else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				Location l = event.getClickedBlock().getLocation();
				plugin.l2.put(event.getPlayer(), event.getClickedBlock().getLocation());
				event.setCancelled(true);

				event.getPlayer().sendMessage(
						"§aLocation 1 set to (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ")");
				return;
			}
		} else {
			return;
		}
	}
}
