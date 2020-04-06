package com.ItsAloof;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.ItsAloof.Arenas.JoinArena;
import com.ItsAloof.Commands.MSpleefCommand;
import com.ItsAloof.Listeners.BreakBlock;
import com.ItsAloof.Listeners.GameListener;
import com.ItsAloof.Listeners.LocationListener;
import com.ItsAloof.signs.Signs;
import com.ItsAloof.utils.Utils;

public class HubSpleefPlugin extends JavaPlugin {
	public List<String> enabledArenas = new ArrayList<String>();
	public List<String> queued = new ArrayList<String>();
	public HashMap<String, List<Player>> enabled = new HashMap<String, List<Player>>();
	public HashMap<String, List<Location>> arenas = new HashMap<String, List<Location>>();
	public HashMap<Player, Location> l1 = new HashMap<Player, Location>();
	public HashMap<Player, Location> l2 = new HashMap<Player, Location>();
	public HashMap<Player, HashMap<Integer, ItemStack>> inventory = new HashMap<Player, HashMap<Integer, ItemStack>>();
	public HashMap<String, List<Location>> blocks = new HashMap<String, List<Location>>();
	public List<Player> noFall = new ArrayList<Player>();
	public HashMap<String, List<Location>> signs = new HashMap<String, List<Location>>();
	public List<Player> edit = new ArrayList<Player>();
	Utils utils = new Utils(this);
	public String prefix;

	@Override
	public void onEnable() {
		getArenas();
		utils.startFallChecker();
		Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
		Bukkit.getPluginManager().registerEvents(new BreakBlock(this), this);
		Bukkit.getPluginManager().registerEvents(new LocationListener(this), this);
		Bukkit.getPluginManager().registerEvents(new Signs(this), this);
		getCommand("mspleef").setExecutor(new MSpleefCommand(this));
		saveDefaultConfig();
		JoinArena join = new JoinArena(this);
		join.startChecker();
		utils.signUpdated();
		createFile();
		prefix = getLanguage().getString("Prefix").replace("&", "§");
	}

	public void createFile() {
		getDataFolder().mkdirs();
		File f = new File(getDataFolder(), "language.yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
				setMessages(f);
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	public void setMessages(File f) {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		fc.createSection("Prefix");
		fc.createSection("Win");
		fc.createSection("Join");
		fc.createSection("Starting");
		fc.createSection("Starting-Arena");
		fc.createSection("leave");
		fc.createSection("Start-Game");
		fc.createSection("Create-Arena");
		fc.createSection("Cancelled");
		fc.set("Starting-Arena", "&aGame starting in &6{x}  &aseconds!");
		fc.set("Cancelled", "&cGame Cancelled, not enough players!");
		fc.set("Prefix", "&bMiniSpleef &f>");
		fc.set("Starting", "&6{arena} &ais starting in &6{x} &aseconds!");
		fc.set("Create-Arena", "&aSuccessfully created &6{arena}&a!");
		try {
			fc.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FileConfiguration getLanguage() {
		File f = new File(getDataFolder(), "language.yml");
		if (!f.exists()) {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			return fc;
		} else {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			return fc;
		}
	}

	public Location getLoc2(String arena) {
		return this.arenas.get(arena).get(0);
	}

	public Location getLoc1(String arena) {
		return arenas.get(arena).get(1);
	}

	public boolean isInRect(Player player, Location loc1, Location loc2) {
		double[] dim = new double[2];

		dim[0] = loc1.getX();
		dim[1] = loc2.getX();
		Arrays.sort(dim);
		if (player.getLocation().getX() > dim[1] || player.getLocation().getX() < dim[0])
			return false;
		dim[0] = loc1.getY();
		dim[1] = loc2.getY();
		Arrays.sort(dim);
		if (player.getLocation().getY() > dim[1] || player.getLocation().getY() < dim[0])
			return false;

		dim[0] = loc1.getZ();
		dim[1] = loc2.getZ();
		Arrays.sort(dim);
		if (player.getLocation().getZ() > dim[1] || player.getLocation().getZ() < dim[0])
			return false;

		return true;
	}

	public void getArenas() {
		Location l1 = null;
		Location l2 = null;
		Location spawn = null;
		if (getConfig().getConfigurationSection("Arenas") == null) {
			return;
		} else {
			for (String arena : getConfig().getConfigurationSection("Arenas").getKeys(false)) {
				List<String> locations = getConfig().getStringList("Arenas." + arena + ".Locations");

				for (String string : locations) {
					if (string.startsWith("l1")) {
						string.replace("l1", "");
						String[] loc = string.split(" ");
						l1 = getLocation(loc);

					} else if (string.startsWith("l2")) {
						string.replace("l2", "");
						String[] loc = string.split(" ");
						l2 = getLocation(loc);
					} else if (string.startsWith("spawn")) {
						string.replace("spawn", "");
						String[] loc = string.split(" ");
						spawn = getLocation(loc);
					}
				}
				List<Location> temp = new ArrayList<Location>();
				temp.add(l1);
				temp.add(l2);
				temp.add(spawn);
				arenas.put(arena, temp);
				continue;
			}
		}
		return;
	}

	public boolean checkFull(String arena) {
		if (enabled.get(arena).size() >= getMaxPlayers(arena)) {
			CloseDoors(arena);
			return true;
		} else {
			return false;
		}
	}

	public ItemStack setMeta(ItemStack item, Player player, String name) {
		if (player.getName().equals("ItsAloof") || player.getName().equals("i_am_awesome979")
				|| player.getName().equals("Postknowledge")) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.addEnchant(Enchantment.DURABILITY, 10, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			return item;
		} else {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			item.setItemMeta(meta);
			return item;
		}
	}

	public void startGame(String arena) {
		BukkitRunnable br = new BukkitRunnable() {
			int x = 31;

			@Override
			public void run() {
				if (checkPlayers(arena)) {
					sendArenaMsg(arena, getLanguage().getString("Cancelled"));
					queued.remove(arena);
					this.cancel();
				} else {
					x--;
					if (x == 30) {
						Bukkit.broadcastMessage(prefix
								+ getLanguage().getString("Starting").replace("{arena}", arena).replace("{x}", x + ""));
						sendArenaMsg(arena, getLanguage().getString("Starting-Arena").replace("{arena}", arena)
								.replace("{x}", x + ""));
						return;
					} else if (x < 10 && x > 0) {
						sendArenaMsg(arena, "§a" + x + "...");
						return;
					} else if (x == 0) {
						for (Player player : enabled.get(arena)) {
							player.getInventory().addItem(setMeta(new ItemStack(Material.DIAMOND_SPADE), player,
									"§b" + player.getName() + "'s Shovel"));
						}
						sendArenaMsg(arena, "§aGame has started!");
						queued.remove(arena);
						enabledArenas.add(arena);
						CloseDoors(arena);
						removeMounts(arena);
						this.cancel();
						return;
					} else if (x == 15) {
						Bukkit.broadcastMessage(prefix
								+ getLanguage().getString("Starting").replace("{arena}", arena).replace("{x}", x + ""));
						sendArenaMsg(arena, getLanguage().getString("Starting-Arena").replace("{arena}", arena)
								.replace("{x}", x + ""));
					}
				}

			}
		};
		br.runTaskTimer(this, 1L, 20L);
	}

	public void removeMounts(String arena) {
		for (Player player : this.enabled.get(arena)) {
			player.setSneaking(true);
			player.setSneaking(false);
		}
	}

	public boolean checkPlayers(String arena) {
		if (this.enabled.get(arena).size() >= getConfig().getInt("Arenas." + arena + ".minPlayers")) {
			return false;
		} else {
			return true;
		}
	}

	public int getMinPlayers(String arena) {
		return getConfig().getInt("Arenas." + arena + ".minPlayers");
	}

	public int getMaxPlayers(String arena) {
		return getConfig().getInt("Arenas." + arena + ".maxPlayers");
	}

	@SuppressWarnings("deprecation")
	public void CloseDoors(String arena) {
		Utils utils = new Utils(this);
		for (String location : utils.getList(arena)) {
			String[] locs = location.split(" ");
			if (Material.getMaterial(locs[4].replace("(", " ").replace(")", "").split(" ")[0]) == null) {
				continue;
			} else if ((locs[4].replace("(", " ").replace(")", "").contains("BOTTOM half of WOODEN_DOOR"))) {
				utils.getLocation(locs).getBlock().setType(Material.WOODEN_DOOR);
				continue;
			} else {
				utils.getLocation(locs).getBlock()
						.setType(Material.getMaterial(locs[4].replace("(", " ").replace(")", "").split(" ")[0]));
				utils.getLocation(locs).getBlock()
						.setData((byte) Integer.parseInt(locs[4].replace("(", " ").replace(")", "").split(" ")[1]));
				continue;
			}
		}
		return;
	}

	public void sendArenaMsg(String arena, String msg) {
		for (Player player : enabled.get(arena)) {
			player.sendMessage(msg);
		}
		return;
	}

	public void ArenaJoin(Player player, String arena, String msg) {
		for (Player p : enabled.get(arena)) {
			if (p != player) {
				p.sendMessage(msg);
			} else {
				continue;
			}
		}
		return;
	}

	public boolean canStart(String arena) {
		int minPlayer = getConfig().getInt("Arenas." + arena + ".minPlayers");
		int players = 0;
		for (@SuppressWarnings("unused")
		Player player : enabled.get(arena)) {
			players++;
		}
		if (players >= minPlayer) {
			return true;
		} else {
			return false;
		}
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

	public int getInt(String integer) {
		try {
			return Integer.parseInt(integer);
		} catch (NumberFormatException e) {
			Bukkit.broadcastMessage(e.toString());
			return 0;
		}
	}

	public boolean disableCommands(Player player) {
		for (String arena : arenas.keySet()) {
			if (enabled.get(arena) == null) {
				continue;
			} else {
				if (enabled.get(arena).contains(player)) {
					return true;
				} else {
					continue;
				}
			}
		}
		return false;
	}

}
