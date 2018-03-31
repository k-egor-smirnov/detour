package ru.alphach1337.detour.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.sqlite.DataBase;

import java.util.ArrayList;
import java.util.UUID;

class Quit {
    Quit(PlayerQuitEvent event) {
        ArrayList<String> players = DataBase.selectAll("players");
        ArrayList<String> ignorePlayers = DataBase.selectAll("ignorePlayers");
        ArrayList<String> party = DataBase.selectAll("party");
            for(int i = 0; i < players.size(); i++){
                if(event.getPlayer().getUniqueId().equals(UUID.fromString(players.get(i)))) {
                    if ((!DetourManager.getInstance().config.getBoolean("allowOffline"))) {
                        DataBase.delete(players.get(i), "players");
                        DataBase.delete(ignorePlayers.get(i), "players");
                    } else {
                        DataBase.delete(players.get(i), "locations");
                        DataBase.insert(players.get(i), event.getPlayer().getLocation(), "locations");
                    }
                }
            for(int j = 0; j < party.size(); j++){
                Log.info(event.getPlayer().getName());
                Log.info(party.get(j));

                if(event.getPlayer().getUniqueId().equals(UUID.fromString(party.get(j)))){
                    DataBase.delete(party.get(j), "party");

                    for(String username : party){
                        Bukkit.getPlayer(UUID.fromString(username)).sendMessage(ChatColor.RED + "Игрок " + event.getPlayer().getName() + " вышел из группы");
                    }
                }
            }

        }
    }
}
