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
import ru.alphach1337.detour.sqlite.DataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Interact {
    public Interact(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            ArrayList<String> players = DataBase.selectAll("players");
            ArrayList<String> party = DataBase.selectAll("party");
            HashMap<String, Location> locations = DataBase.selectAllLocations("locations");
            if (player.isOp() && player.getInventory().getItemInMainHand().getType() == Material.STICK && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(Settings.stick)) {
                if (event.getAction() == Action.LEFT_CLICK_AIR && players.size() >= 0) {
                    if (DetourManager.getInstance().getIsDetour()) {
                        for (String username : party) {
                            Bukkit.getPlayer(UUID.fromString(username)).teleport(locations.get(players.get(0)));
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
        }catch (Exception e){
            Bukkit.broadcastMessage(e.getMessage());
        }
    }

    private void next(Player p) {
        boolean isOffline = false;
        HashMap<String, Location> locations = DataBase.selectAllLocations("locations");
        ArrayList<String> players = DataBase.selectAll("players");
        ArrayList<String> party = DataBase.selectAll("party");

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
            for (String username : party) {
                Bukkit.getPlayer(UUID.fromString(username)).teleport(Bukkit.getPlayer(UUID.fromString(players.get(0))));
            }
        } catch (Exception e) {
            for (String username : party) {
                Bukkit.getPlayer(UUID.fromString(username)).teleport(locations.get(players.get(0)));
            }
            isOffline = true;
        }

        for (int i = 0; i < players.size(); i++) {
            Player player = Bukkit.getPlayer(UUID.fromString(players.get(i)));

            if (player != null && player.isOnline()) {
                if (i > 0) {
                    p.sendMessage(ChatColor.GREEN + "Твое место в очереди " + i);
                    //ActionBarAPI.sendActionBar(player, ChatColor.GREEN + "Твое место в очереди " + i, 200);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "За вами наблюдают!");
                    //ActionBarAPI.sendActionBar(player, ChatColor.YELLOW + "За вами наблюдают!", 500);
                }
            }
        }

        for (String s : locations.keySet()) {
            if (players.get(0).equals(s)) {
                if (!isOffline) {
                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + Bukkit.getPlayer(UUID.fromString(s)).getName());
                    p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (players.size() - 1));
                } else {
                    String str = DataBase.selectNameById(s, "idandname");

                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + str + ChatColor.RED + " (оффлайн)");
                    p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (players.size() - 1));
                }
                DataBase.delete(players.get(0), "players");
                return;
            }
        }
    }
}
