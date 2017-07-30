package ru.alphach1337.detour.commands;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Detour;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Next implements Command{

    @Override
    public String getPermission() {
        return "detour.manage";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void execute(CommandSender commandSender, org.bukkit.command.Command command, String[] args) {
        if(!DetourManager.getInstance().getIsDetour()){
            commandSender.sendMessage(Settings.notStarted);
            return;
        }

        boolean isOffline = false;

        ArrayList<String> players = DetourManager.getInstance().players;
        HashMap<String, Location> locations = DetourManager.getInstance().locations;
        ArrayList<String> ignorePlayers = DetourManager.getInstance().ignorePlayers;

        Player p = (Player) commandSender;

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
            for(String username : DetourManager.getInstance().party) {
                Bukkit.getPlayer(username).teleport(Bukkit.getPlayer(players.get(0)));
            }

        } catch (Exception e) {
            for(String username : DetourManager.getInstance().party) {
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
