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
    HashMap<String, Location> loc = new HashMap<String, Location>();
    ArrayList<String> players = new ArrayList<String>();
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
            for (String s : loc.keySet()) {
                if (p.getName().equals(loc.get(s))) {
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
            loc.put(p.getName(), l.clone());
            players.add(p.getName());
            ignorePlayers.add(p.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("next")) {
            boolean isOffline = false;
            if (!p.isOp()) {
                p.sendMessage(settings.hasNoPermission);
                return true;
            }
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            if (players.isEmpty()) {
                p.sendMessage(settings.notJoined);
                return true;
            }

            try {
                p.teleport(Bukkit.getPlayer(players.get(0)));
            } catch (Exception e) {
                p.teleport(loc.get(players.get(0)));
                isOffline = true;
            }

            for (String s : loc.keySet()) {
                if (players.get(0).equals(s)) {
                    players.remove(0);
                    loc.remove(s);
                    if (!isOffline) {
                        p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s);
                        p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + players.size());
                    } else {
                        p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s + ChatColor.RED + " (оффлайн)");
                        p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + players.size());
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
            Title title = new Title(settings.Started1, "Чтобы присоединиться, пиши /detour join");
            title.setSubtitleColor(ChatColor.YELLOW);
            title.broadcast();
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
            players.clear();
            loc.clear();
            ignorePlayers.clear();
            Bukkit.broadcastMessage(settings.stopDetour);
            return true;

        }

        return true;
    }

}