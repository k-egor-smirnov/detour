package ru.alphach1337.detour.events;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Interact {
    public Interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && player.getInventory().getItemInMainHand().getType() == Material.STICK && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(Settings.stick)) {
            if (event.getAction() == Action.LEFT_CLICK_AIR && DetourManager.getInstance().players.size() >= 0) {
                if (DetourManager.getInstance().getIsDetour()) {
                    Player target = Bukkit.getPlayer(DetourManager.getInstance().players.get(0));

                    if (target.isOnline()) {
                        for (String username : DetourManager.getInstance().party) {
                            Bukkit.getPlayer(username).teleport(target);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Игрок вышел из сети");
                    }
                } else {
                    player.sendMessage(Settings.notStarted);
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (DetourManager.getInstance().getIsDetour()) {
                    next(player);
                } else {
                    player.sendMessage(Settings.notStarted);
                }
            }
        }
    }

    private void next(Player p) {
        boolean isOffline = false;

        ArrayList<String> players = DetourManager.getInstance().players;
        HashMap<String, Location> locations = DetourManager.getInstance().locations;
        ArrayList<String> ignorePlayers = DetourManager.getInstance().ignorePlayers;

        if (players.size() <= 0) {
            DetourManager.getInstance().stop();
            Bukkit.broadcastMessage(Settings.stopDetour);

            return;
        }

        if (!DetourManager.getInstance().getIsDetour()) {
            p.sendMessage(Settings.notStarted);
            return;
        }

        if (players.isEmpty()) {
            p.sendMessage(Settings.notJoined);
            return;
        }

        try {
            for (String username : DetourManager.getInstance().party) {
                Bukkit.getPlayer(username).teleport(Bukkit.getPlayer(players.get(0)));
            }
        } catch (Exception e) {
            for (String username : DetourManager.getInstance().party) {
                Bukkit.getPlayer(username).teleport(locations.get(players.get(0)));
            }
            isOffline = true;
        }

        for (int i = 0; i < players.size(); i++) {
            Player player = Bukkit.getPlayer(players.get(i));

            if (player != null && player.isOnline()) {
                if (i > 0) {
                    ActionBarAPI.sendActionBar(player, ChatColor.GREEN + "Твое место в очереди " + i, 200);
                } else {
                    ActionBarAPI.sendActionBar(player, ChatColor.YELLOW + "За вами наблюдают!", 500);
                }
            }
        }

        for (String s : locations.keySet()) {
            if (players.get(0).equals(s)) {
                if (!isOffline) {
                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s);
                    p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (players.size() - 1));
                } else {
                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s + ChatColor.RED + " (оффлайн)");
                    p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (players.size() - 1));
                }


                players.remove(0);
                return;
            }
        }

    }
}
