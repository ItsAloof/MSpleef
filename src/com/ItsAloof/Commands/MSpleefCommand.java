package com.ItsAloof.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ItsAloof.HubSpleefPlugin;
import com.ItsAloof.API.APIClass;
import com.ItsAloof.Arenas.ArenaBuilder;
import com.ItsAloof.utils.Utils;

public class MSpleefCommand implements CommandExecutor {
	HubSpleefPlugin plugin;
	Utils utils;

	public MSpleefCommand(HubSpleefPlugin plugin) {
		this.plugin = plugin;
		this.utils = new Utils(plugin);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("mspleef")) {
				if (args.length == 0 && sender.hasPermission("minispleef.permissions.view")) {
					sender.sendMessage(ChatColor.RED + ">/mspleef [arenaname] [minplayer] [maxplayer]");
					sender.sendMessage("§c>/mspleef remove [arena]");
					sender.sendMessage("§c>/mspleef list");
					sender.sendMessage("§c>/mspleef entrance [arena]");
					sender.sendMessage("§c>/mspleef edit");
					return true;
				} else if (!sender.hasPermission("minispleef.permissions.view")) {
					sender.sendMessage("§cInsufficient Permissions!");
					return false;
				}
				if (args.length == 3) {
					if (checkArena(args[0]) && sender.hasPermission("minispleef.create")) {
						createArena((Player) sender, args[0], getInt(args[1]), getInt(args[2]));
						return true;
					} else {
						sender.sendMessage("§cThat arena already exists!");
						return true;
					}
				} else {
					if (RemoveCommand(sender, args)) {
						return true;
					} else if (listCommand(sender, args)) {
						return true;
					} else if (args.length == 2 && args[0].equalsIgnoreCase("entrance")) {
						APIClass api = new APIClass(plugin);
						if (api.getArena(args[1]) != null) {
							ArenaBuilder builder = new ArenaBuilder(plugin);
							builder.setDoor(args[1], plugin.l1.get((Player) sender), plugin.l2.get((Player) sender));
							sender.sendMessage("§aSuccessfully set entrance for " + args[1]);
							return true;
						} else {
							sender.sendMessage("§cError 404: §7Could not find " + args[1] + "!");
							return true;
						}
					} else if (args.length == 1 && args[0].equalsIgnoreCase("edit")
							&& sender.hasPermission("minispleef.edit")) {
						if (!plugin.edit.contains((Player) sender)) {
							plugin.edit.add((Player) sender);
							sender.sendMessage("§aYou are now in arena edit mode!");
							return true;
						} else {
							plugin.edit.remove((Player) sender);
							sender.sendMessage("§cYou are no longer in arena edit mode!");
							return true;
						}
					} else if (sender.hasPermission("minispleef.permissions.view")
							&& args[0].equalsIgnoreCase("entrance")) {
						sender.sendMessage("§c>/mspleef entrance [arena]");
						return false;
					} else {
						if (sender.hasPermission("minispleef.permissions.view")) {
							sender.sendMessage(ChatColor.RED + ">/mspleef [arenaname] [minplayer] [maxplayer]");
							sender.sendMessage("§c>/mspleef remove [arena]");
							sender.sendMessage("§c>/mspleef list");
							sender.sendMessage("§c>/mspleef entrance [arena]");
							sender.sendMessage("§c>/mspleef edit");
							return false;
						} else {
							sender.sendMessage("§cInsufficient Permissions!");
							return false;
						}
					}
				}
			}
		} else {
			sender.sendMessage("§cConsole cannot use that command!");
			return true;
		}
		return false;

	}

	public boolean RemoveCommand(CommandSender sender, String[] args) {
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("minispleef.remove")) {
				utils.removeArena((Player) sender, args[1]);
				return true;
			} else {
				return false;
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			sender.sendMessage("§c>/mspleef remove [arena]");
			return true;
		}
		return false;
	}

	public boolean listCommand(CommandSender sender, String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("list") && sender.hasPermission("minispleef.list")) {
			utils.listArenas((Player) sender);
			return true;
		} else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("minispleef.reload")) {
			utils.reloadConfig(sender);
			return true;
		} else {
			return false;
		}
	}

	public int getInt(String integer) {
		try {
			return Integer.parseInt(integer);
		} catch (NumberFormatException e) {
			Bukkit.broadcastMessage(e.toString());
			return 0;
		}
	}

	public void createArena(Player player, String name, int minPlayer, int maxPlayer) {
		if (minPlayer < maxPlayer && !utils.isInArena(player.getLocation())) {
			ArenaBuilder builder = new ArenaBuilder(plugin);
			builder.l1 = plugin.l1.get(player);
			builder.l2 = plugin.l2.get(player);
			builder.spawn = player.getLocation();
			builder.name = name;
			builder.minPlayers = minPlayer;
			builder.maxPlayers = maxPlayer;
			builder.buildArena(player);
			return;
		} else if (utils.isInArena(player.getLocation())) {
			player.sendMessage("§cYou must leave the arena boundaries before creating the arena!");
			return;
		} else {
			player.sendMessage("§cThe maximum player count must be greater than the minimum!");
			return;
		}
	}

	public boolean checkArena(String name) {
		for (String a : plugin.arenas.keySet()) {
			if (a.equalsIgnoreCase(name)) {
				return false;
			} else {
				continue;
			}
		}
		return true;
	}

}
