package ru.alphach1337.detour.events;

import org.bukkit.*;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Interact {
    public Interact(PlayerInteractEvent event) {
        DetourManager detourManager = DetourManager.getInstance();
        Database database = Database.getInstance();

        try {
            Player player = event.getPlayer();
            if (
                    player.isOp() &&
                            player.getInventory().getItemInMainHand().getType() == Material.STICK &&
                            player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(Settings.stick)
            ) {
                if (detourManager.getIsDetour()) {
                    ArrayList<EventParticipant> players =
                            database.getPlayers(detourManager.getEventId(), false, false);
                    ArrayList<EventParticipant> reviewers =
                            database.getPlayers(detourManager.getEventId(), false, true);

                    if (event.getAction() == Action.LEFT_CLICK_AIR) {
                        for (EventParticipant reviewer : reviewers) {
                            Bukkit.getPlayer(reviewer.getUUID()).teleport(players.get(0).getLocation());
                        }

                    } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        next(player);

                    }
                } else {
                    player.sendMessage(Settings.notStarted);
                }
            }
        } catch (Exception e) {
            Bukkit.broadcastMessage(e.getMessage());
        }
    }

    private void next(Player p) {
        DetourManager.getInstance().next(p);
    }
}
