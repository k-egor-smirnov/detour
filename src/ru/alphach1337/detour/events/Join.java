package ru.alphach1337.detour.events;

import org.bukkit.event.player.PlayerJoinEvent;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

class Join {
    Join(PlayerJoinEvent event){
        if (DetourManager.getInstance().getIsDetour()) {
            event.getPlayer().sendMessage(Settings.Started1);
            event.getPlayer().sendMessage(Settings.Started2);
        }

        // Привет, админ, к обходу готово уже x человек
    }
}
