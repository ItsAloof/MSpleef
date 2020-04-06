package com.ItsAloof.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.ItsAloof.HubSpleefPlugin;

public class Utils {
	HubSpleefPlugin plugin;

	public Utils(HubSpleefPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean isInArena(Location l) {
		for (String arena : plugin.arenas.keySet()) {
			if (isInRect(l, getLoc1(arena), getLoc2(arena))) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	public boolean blockInArena(Location l) {
		for (String arena : plugin.arenas.keySet()) {
			for (Location loc : genCube(getLoc1(arena), getLoc2(arena))) {
				if (loc == l) {
					return true;
				} else {
					continue;
				}
			}
		}
		return false;
	}

	public Location getLoc1(String arena) {
		return plugin.arenas.get(arena).get(0);
	}

	public Location getLoc2(String arena) {
		return plugin.arenas.get(arena).get(1);
	}

	public void setInventory(Player player) {
		if (plugin.inventory.containsKey(player)) {
			player.getInventory().clear();
			for (int x = 0; x <= player.getInventory().getSize(); x++) {
				for (int y : plugin.inventory.get(player).keySet()) {
					if (x == y) {
						player.getInventory().setItem(x, plugin.inventory.get(player).get(x));
						continue;
					} else {
						continue;
					}
				}
			}
		}
		plugin.inventory.remove(player);
	}

	public void saveInventory(Player player) {
		HashMap<Integer, ItemStack> i = new HashMap<Integer, ItemStack>();
		for (int x = 0; x <= player.getInventory().getSize(); x++) {
			if (player.getInventory().getItem(x) != null) {
				i.put(x, player.getInventory().getItem(x));
			} else {
				continue;
			}
		}
		plugin.inventory.put(player, i);
		return;
	}

	public String getPlayersArena(Player player) {
		for (String arena : plugin.enabled.keySet()) {
			if (plugin.enabled.get(arena).contains(player)) {
				return arena;
			} else {
				continue;
			}
		}
		return null;
	}

	public void setBlocks(String arena) {
		for (Location l : plugin.blocks.get(arena)) {
			l.getBlock().setType(Material.SNOW_BLOCK);
			continue;
		}
		plugin.blocks.remove(arena);
		return;
	}

	public Location getLocation(String[] string) {
		int x = 0;
		int y = 0;
		int z = 0;
		World world = null;
		for (String coord : string) {
			if (coord.contains("x")) {
				x = getInt(coord.split(":")[1]);
			} else if (coord.contains("y")) {
				y = getInt(coord.split(":")[1]);
			} else if (coord.contains("z")) {
				z = getInt(coord.split(":")[1]);
			} else if (coord.contains("world")) {
				world = Bukkit.getWorld(coord.split(":")[1]);
			}
		}
		return new Location(world, x, y, z);
	}

	public List<String> getList(String arena) {
		List<String> locations = plugin.getConfig().getStringList("Arenas." + arena + ".Entrances");
		return locations;
	}

	public int getInt(String integer) {
		try {
			return Integer.parseInt(integer);
		} catch (NumberFormatException e) {
			Bukkit.broadcastMessage(e.toString());
			return 0;
		}
	}

	public List<Location> genCube(Location l1, Location l2) {
		List<Location> temp = new ArrayList<Location>();
		int MinX, MaxX, MinY, MaxY, MinZ, MaxZ;
		if (l1.getBlockX() < l2.getBlockX()) {
			MinX = l1.getBlockX();
			MaxX = l2.getBlockX();
		} else {
			MinX = l2.getBlockX();
			MaxX = l1.getBlockX();
		}
		if (l1.getBlockY() < l2.getBlockY()) {
			MinY = l1.getBlockY();
			MaxY = l2.getBlockY();
		} else {
			MinY = l2.getBlockY();
			MaxY = l1.getBlockY();
		}
		if (l1.getBlockZ() < l2.getBlockZ()) {
			MinZ = l1.getBlockZ();
			MaxZ = l2.getBlockZ();
		} else {
			MinZ = l2.getBlockZ();
			MaxZ = l1.getBlockZ();
		}
		for (int x = MinX; x <= MaxX; x++) {
			for (int y = MinY; y <= MaxY; y++) {
				for (int z = MinZ; z <= MaxZ; z++) {
					Location l = new Location(l1.getWorld(), x, y, z);
					temp.add(l);
				}
			}
		}
		return temp;
	}

	public void listArenas(Player player) {
		int x = 1;
		player.sendMessage("§6-=§aArenas§6=-");
		for (String arena : plugin.arenas.keySet()) {
			player.sendMessage(x + ". " + arena);
			x++;
		}
		return;
	}

	public void removeArena(Player player, String arena) {
		for (String arenas : plugin.arenas.keySet()) {
			if (arenas.equalsIgnoreCase(arena)) {
				plugin.getConfig().set("Arenas." + arenas + ".Locations", null);
				plugin.getConfig().set("Arenas." + arenas + ".maxPlayers", null);
				plugin.getConfig().set("Arenas." + arenas + ".minPlayers", null);
				plugin.getConfig().set("Arenas." + arenas + ".Entrances", null);
				plugin.getConfig().set("Arenas." + arenas, null);
				plugin.saveConfig();
				plugin.reloadConfig();
				plugin.enabled.remove(arenas);
				plugin.arenas.remove(arenas);
				plugin.enabledArenas.remove(arenas);
				player.sendMessage("§aSuccessfully removed §6§n" + arenas);
				return;
			} else {
				continue;
			}
		}
		player.sendMessage("§cEror 404: §4Could not find §7§n" + arena);
		return;
	}

	public void startFallChecker() {
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				for (String arena : plugin.enabled.keySet()) {
					if (plugin.enabled.isEmpty()) {
						return;
					} else if (!plugin.enabled.isEmpty() && plugin.enabled.get(arena) != null) {
						for (Player player : plugin.enabled.get(arena)) {
							player.setFoodLevel(20);
							player.setSaturation(10);
							if (checkBlock(player.getLocation()) && !plugin.enabledArenas.contains(arena)) {
								plugin.noFall.add(player);
								player.teleport(plugin.arenas.get(arena).get(2));
								continue;
							} else if (checkBlock(player.getLocation()) && plugin.enabledArenas.contains(arena)) {
								plugin.noFall.add(player);
								launchFirework(player.getLocation());
								player.teleport(plugin.arenas.get(arena).get(2));
								plugin.enabled.get(arena).remove(player);
								setInv(player);
								if (checkEnd(arena))
									return;
								continue;
							}
						}
					} else {
						continue;
					}

				}
			}

		};
		br.runTaskTimer(plugin, 10L, 10L);
	}

	public void reloadConfig(CommandSender sender) {
		plugin.saveConfig();
		plugin.reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "Successfully reloaded config!");
		return;
	}

	public boolean checkEnd(String arena) {
		if (plugin.enabled.get(arena).size() == 1 && plugin.enabledArenas.contains(arena)) {
			Bukkit.broadcastMessage(plugin.prefix + " §6" + plugin.enabled.get(arena).get(0).getName()
					+ "§a has won in §6" + arena + "§a!");
			Player player = plugin.enabled.get(arena).get(0);
			plugin.enabled.get(arena).get(0).teleport(plugin.arenas.get(arena).get(2));
			setupArena(arena);
			setInv(player);
			return true;
		} else {
			return false;
		}
	}

	public void launchFirework(Location l) {
		FireworkEffect.Builder b = FireworkEffect.builder();
		b.flicker(true);
		b.with(FireworkEffect.Type.BALL_LARGE);
		b.withColor(Color.RED);
		b.withFade(Color.WHITE);
		b.withTrail();
		FireworkEffect e = b.build();
		final Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
		FireworkMeta meta = fw.getFireworkMeta();
		meta.addEffect(e);
		meta.setPower(0);
		fw.setFireworkMeta(meta);
		BukkitRunnable br = new BukkitRunnable() {
			public void run() {
				fw.detonate();
			}
		};
		br.runTaskLater(plugin, 1L);
	}

	public void setupArena(String arena) {
		plugin.enabledArenas.remove(arena);
		plugin.enabled.remove(arena);
		setBlocks(arena);
		openDoors(arena);
	}

	public void setInv(Player player) {
		setInventory(player);
		return;
	}

	public void openDoors(String arena) {
		for (String location : getList(arena)) {
			String[] locs = location.split(" ");
			getLocation(locs).getBlock().setType(Material.AIR);
			continue;
		}
		return;
	}

	public void openDoors2(String arena) {
		Utils utils = new Utils(plugin);
		for (Location l : genCube(plugin.arenas.get(arena).get(0), plugin.arenas.get(arena).get(1))) {
			for (String location : utils.getList(arena)) {
				String[] locs = location.split(" ");
				if (l == utils.getLocation(locs)) {
					l.getBlock().setType(Material.AIR);
					continue;
				} else {
					continue;
				}
			}
		}

		return;
	}

	public boolean checkBlock(Location l) {
		if (l.getBlock().getRelative(BlockFace.DOWN).getType() == Material.PACKED_ICE) {
			return true;
		} else {
			return false;
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

	public void signUpdated() {
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				if (plugin.signs.isEmpty()) {
					return;
				} else {
					for (String arena : plugin.signs.keySet()) {
						for (Location l : plugin.signs.get(arena)) {
							if (!plugin.arenas.containsKey(arena)) {
								plugin.signs.remove(arena);
							} else {

								if (isSign(l)) {
									Sign s = (Sign) l.getBlock().getState();
									if (plugin.enabledArenas.contains(arena)) {
										s.setLine(2, "§cGame Started");
										s.setLine(3,
												plugin.enabled.get(arena).size() + "/" + plugin.getMaxPlayers(arena));
										s.update();
										continue;
									} else {
										if (plugin.enabled.containsKey(arena)) {
											s.setLine(2, "§aJoinable");
											s.setLine(3, plugin.enabled.get(arena).size() + "/"
													+ plugin.getMaxPlayers(arena));
											s.update();
											continue;
										} else {
											s.setLine(2, "§aJoinable");
											s.setLine(3, "0/" + plugin.getMaxPlayers(arena));
											s.update();
											continue;
										}
									}
								} else {
									if (plugin.signs.get(arena).size() == 1) {
										plugin.signs.remove(arena);
										continue;
									} else {
										plugin.signs.get(arena).remove(l);
										continue;
									}
								}

							}
						}
					}
					return;
				}
			}
		};
		br.runTaskTimer(plugin, 10L, 10L);
	}

	public boolean isSign(Location l) {
		if (l.getBlock().getType() == Material.WALL_SIGN || l.getBlock().getType() == Material.SIGN_POST) {
			return true;
		} else {
			return false;
		}
	}
}
