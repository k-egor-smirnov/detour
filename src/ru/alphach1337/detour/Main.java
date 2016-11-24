package ru.alphach1337.detour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;


public class Main extends JavaPlugin implements Listener {
    HashMap<Location, String> loc2 = new HashMap<Location, String>();
    ArrayList<Location> loc = new ArrayList<Location>();
    ArrayList<String> ignorePlayers = new ArrayList<String>();
    public boolean isDetour = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (isDetour) {
            event.getPlayer().sendMessage(settings.Started1);
            event.getPlayer().sendMessage(settings.Started2);
        }
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
        }
        if (args.length == 0) {
            return false;
        }

        Player p = (Player) sender;
        if (args[0].equalsIgnoreCase("join")) {
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            for (Location l2 : loc2.keySet()) {
                if (p.getName().equals(loc2.get(l2))) {
                    p.sendMessage(settings.alreadyInTheList);
                    return true;
                }
            }
            if (ignorePlayers.contains(p.getName())) {
                p.sendMessage(settings.alreadyInTheList);
                return true;
            }
            p.sendMessage(settings.addedToList);
            Location l = p.getLocation().clone();
            loc2.put(l.clone(), p.getName());
            loc.add(l.clone());
            return true;
        }
        if (args[0].equalsIgnoreCase("next")) {
            if (!p.isOp()) {
                p.sendMessage(settings.hasNoPermission);
                return true;
            }
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            if (loc.isEmpty()) {
                p.sendMessage(settings.notJoined);
                return true;
            }
            p.teleport(loc.get(0));
            for (Location st : loc2.keySet()) {
                if (loc.get(0).equals(st)) {
                    String pl = loc2.get(st);
                    loc.remove(0);
                    loc2.remove(st);
                    ignorePlayers.add(pl);
                    try {
                        Player p2 = Bukkit.getPlayer(pl);
                        p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + p2.getName());
                        p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + loc.size());
                    } catch (Exception e) {
                        p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + pl + ChatColor.RED + " (оффлайн)");
                        p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + loc.size());
                    }
                    return true;
                }
            }
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!p.isOp()) {
                p.sendMessage(settings.hasNoPermission);
                return true;
            }
            if (isDetour) {
                p.sendMessage(settings.alreadyStarted);
                return true;
            }
            Bukkit.broadcastMessage(settings.Started1);
            Bukkit.broadcastMessage(settings.Started2);
            isDetour = true;
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!p.isOp()) {
                p.sendMessage(settings.hasNoPermission);
                return true;
            }
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            isDetour = false;
            loc.clear();
            loc2.clear();
            ignorePlayers.clear();
            Bukkit.broadcastMessage(settings.stopDetour);
            return true;

        }

        return true;
    }

}