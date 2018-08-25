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

        if (!allowOffline) {
            database.removePlayerFromEvent(detourManager.getEventId(), new EventParticipant(
                    detourManager.getEventId(),
                    event.getPlayer()
            ));
        }
    }
}
