package ru.alphach1337.detour.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.alphach1337.detour.Detour;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new Join(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Log.info(event.getPlayer());
        new Quit(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        new Interact(event);
    }
}
