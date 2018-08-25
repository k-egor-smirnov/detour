package ru.alphach1337.detour.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;
import java.util.UUID;

class Quit {
    Quit(PlayerQuitEvent event) {
        DetourManager detourManager = DetourManager.getInstance();
        Database database = Database.getInstance();

        boolean allowOffline = detourManager.getConfig().getBoolean("allowOffline");

        ArrayList<EventParticipant> players = database.getPlayers(detourManager.getEventId(), false, false);

//        for (int i = 0; i < players.size(); i++) {
//            if (event.getPlayer().getUniqueId().equals(players.get(i).getUUID())) {
//                if (allowOffline)) {
//                    Database.delete(players.get(i), "players");
//                    Database.delete(ignorePlayers.get(i), "players");
//                } else {
//                    Database.delete(players.get(i), "locations");
//                    Database.insertUuidAndLocation(players.get(i), event.getPlayer().getLocation(), "locations");
//                }
//            }
//            for (int j = 0; j < party.size(); j++) {
//                /*Log.info(event.getPlayer().getName());
//                Log.info(party.get(j));*/
//
//                if (event.getPlayer().getUniqueId().equals(UUID.fromString(party.get(j)))) {
//                    Database.delete(party.get(j), "party");
//
//                    for (String username : party) {
//                        Bukkit.getPlayer(UUID.fromString(username)).sendMessage(ChatColor.RED + "Игрок " + event.getPlayer().getName() + " вышел из группы");
//                    }
//                }
//            }
//
//        }
    }
}
