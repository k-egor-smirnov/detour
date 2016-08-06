package ru.alphach1337.detour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import ru.alphach1337.detour.settings;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
	public Map<String, Location> playersLocations = new HashMap<String, Location>();
	public Map<String, UUID> playersUniqueIds = new HashMap<String, UUID>();
	public ArrayList<String> playersJoined = new ArrayList<String>();
	public ArrayList<String> doNotDelete = new ArrayList<String>();
	int last = 0;
	public boolean isDetour = false;

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

	}

	@Override
	public void onDisable() {

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		boolean isFound = false;
		if (findDoNotDelete(event.getPlayer().getName())) {
			isFound = true;
			return;
		}
		if (!isFound) {
			delete(event.getPlayer().getName());
			return;
		}

	}

	// Delete player by username from detour if not in doNotDelete HashMap
	public boolean delete(String player) {
		playersLocations.remove(player);
		for (int i = 0; i < playersJoined.size(); i++) {
			if (playersJoined.get(i) == player) {
				playersJoined.remove(i);
			}
		}
		return false;
	}

	public boolean next(CommandSender sender) {
		if (playersJoined.isEmpty()) {
			sender.sendMessage(settings.noPeoples);
			return false;
		}

		if (last < playersJoined.size()) {

			try {
				if (Bukkit.getPlayer(playersJoined.get(last)).isOnline()) {
					teleport(sender.getName(), playersJoined.get(last));
					sender.sendMessage(ChatColor.YELLOW + "»грок " + ChatColor.BLUE + playersJoined.get(last - 1)
							+ ChatColor.AQUA + " (" + last + " из " + playersJoined.size() + ")");
					return true;
				}
			} catch (Exception e) {
				teleport(sender.getName(), playersLocations.get(playersJoined.get(last)));
				sender.sendMessage(ChatColor.YELLOW + "ќффлайн игрок " + ChatColor.BLUE + playersJoined.get(last - 1)
						+ ChatColor.AQUA + " (" + last + " из " + playersJoined.size() + ")");

			}
		} else {
			reset();
			return false;
		}

		return false;

	}

	public int findOnlinePlayer(String player) {
		boolean isFound1 = false;
		boolean isFound2 = false;
		for (Map.Entry<String, Location> pair : playersLocations.entrySet()) {
			if (pair.getKey() == player)
				isFound1 = true;
		}
		for (int i = 0; i < doNotDelete.size(); i++) {
			isFound2 = true;
		}
		if (isFound1 && isFound2) {
			return 2;
		} else if (isFound1) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean findDoNotDelete(String player) {
		for (int i = 0; i < doNotDelete.size(); i++) {
			if (doNotDelete.get(i) == player) {
				return true;
			}
		}
		return false;
	}

	public void reset() {
		isDetour = false;
		playersJoined.clear();
		last = 0;
		playersLocations.clear();
		playersUniqueIds.clear();
		doNotDelete.clear();
		Bukkit.broadcastMessage(settings.stopDetour);
	}

	///////////////////////
	public boolean startDetour() {
		isDetour = !isDetour;
		Bukkit.broadcastMessage(settings.Started1);
		Bukkit.broadcastMessage(settings.Started2);
		return true;
	}

	public boolean stopDetour() {
		reset();
		return true;
	}
	///////////////////////

	/*
	 * Teleport to OnlinePlayer
	 */
	public boolean teleport(String owner, String target) {
		try {
			Bukkit.getPlayer(owner).teleport(Bukkit.getPlayer(target));
			last++;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/*
	 * Teleport to OfflinePlayer
	 */
	public boolean teleport(String owner, Location target) {
		try {
			Bukkit.getPlayer(owner).teleport(target);
			last++;

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		boolean isAdmin = sender.isOp();
		switch (cmd.getName()) {

		case "detour":
			if (!isAdmin) {
				sender.sendMessage(settings.hasNoPermission);
			} else if (!isDetour) {
				startDetour();
			} else {

				stopDetour();
			}
			break;
		case "next":
			if (!isAdmin) {
				sender.sendMessage(settings.hasNoPermission);
				break;
			} else if (!isDetour) {
				sender.sendMessage(settings.notStarted);
				break;
			} else {
				next(sender);
			}
			break;
		case "join":
			if (!isDetour) {
				sender.sendMessage(settings.notStarted);
				break;
			}
			if (playersJoined.isEmpty() && playersLocations.isEmpty()) {
				playersLocations.put(sender.getName(), Bukkit.getPlayer(sender.getName()).getLocation());
				playersJoined.add(sender.getName());

				sender.sendMessage(settings.addedToList);
				break;
			}
			boolean isFound2 = false;
			for (Map.Entry<String, Location> pair : playersLocations.entrySet()) {
				if (pair.getKey() == sender.getName()) {
					for (int i = 0; i < playersJoined.size(); i++) {
						if (playersJoined.get(i) == sender.getName()) {
							isFound2 = true;
							break;
						}
					}
					sender.sendMessage(settings.alreadyInTheList);
					break;
				} else {
					for (int i = 0; i < playersJoined.size(); i++) {
						if (playersJoined.get(i) == sender.getName()) {
							isFound2 = true;
							break;
						}
					}
					if (isFound2) {
						sender.sendMessage(settings.alreadyInTheList);
						break;
					}
					playersLocations.put(sender.getName(), Bukkit.getPlayer(sender.getName()).getLocation());
					playersJoined.add(sender.getName());
					playersUniqueIds.put(sender.getName(), Bukkit.getPlayer(sender.getName()).getUniqueId());
					sender.sendMessage(settings.addedToList);
					break;

				}
			}
			break;
		case "savepos":
			boolean isFound3 = false;
			if (!isDetour) {
				sender.sendMessage(settings.notStarted);
				break;
			}
			for (int i = 0; i < playersJoined.size(); i++) {
				if (playersJoined.get(i) == sender.getName()) {
					isFound3 = true;
				}
			}
			if (findDoNotDelete(sender.getName())) {
				sender.sendMessage(settings.alreadySaved);
			}
			if (!isFound3) {
				break;
			}
			if (isFound3) {
				doNotDelete.add(sender.getName());
				sender.sendMessage(settings.savepos1);
				sender.sendMessage(settings.savepos2);
			}
			break;
		case "find":
			if (!isAdmin) {
				sender.sendMessage(settings.hasNoPermission);
				break;
			} else if (!isDetour) {
				sender.sendMessage(settings.notStarted);
				break;
			} else {
				if (playersJoined.isEmpty()) {
					sender.sendMessage(settings.noPeoples);
					break;
				} else if (last == 0) {
					sender.sendMessage(settings.plsUseNext1);
					sender.sendMessage(settings.plsUseNext2);
					break;
				}
				try {
					if (Bukkit.getPlayer(playersJoined.get(last - 1)).isOnline()) {
						Bukkit.getPlayer(sender.getName()).teleport(Bukkit.getPlayer(playersJoined.get(last - 1)));
						return true;
					}
				} catch (Exception e) {
					Bukkit.getPlayer(sender.getName()).teleport(playersLocations.get(playersJoined.get(last - 1)));
				}
			}
			break;
		}
		return true;
	}
}