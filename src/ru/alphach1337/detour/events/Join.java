package ru.alphach1337.detour.events;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;

class Join {
    Join(PlayerJoinEvent event){
        if (DetourManager.getInstance().getIsDetour()) {
            if (event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Обход уже начался, Вы можете продолжить его" +
                        "с помощью команды\n" +
                        ChatColor.DARK_AQUA + "/detour " + ChatColor.BLUE + "next");
            } else {
                event.getPlayer().sendMessage(Settings.Started1);
                event.getPlayer().sendMessage(Settings.Started2);
            }
        } else {
            if (event.getPlayer().isOp()) {
                ArrayList<EventParticipant> players =
                        Database.getInstance().getPlayers(-1, false, false);

                event.getPlayer().sendMessage(ChatColor.GREEN + "Привет, к обходу готово человек: "
                        + ChatColor.DARK_PURPLE + players.size());
            }
        }
    }
}
