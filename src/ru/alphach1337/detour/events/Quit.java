package ru.alphach1337.detour.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.alphach1337.detour.managers.DetourManager;

class Quit {
    Quit(PlayerQuitEvent event) {
        if (DetourManager.getInstance().getIsDetour()) {
            for(int i = 0; i < DetourManager.getInstance().players.size(); i++){
                if(!DetourManager.getInstance().config.getBoolean("allowOffline")) {
                    if (event.getPlayer().getName().equalsIgnoreCase(DetourManager.getInstance().players.get(i))) {
                        DetourManager.getInstance().players.remove(i);
                    }
                }
            }

            for(int j = 0; j < DetourManager.getInstance().party.size(); j++){
                Log.info(event.getPlayer().getName());
                Log.info(DetourManager.getInstance().party.get(j));

                if(event.getPlayer().getName().equals(DetourManager.getInstance().party.get(j))){
                    DetourManager.getInstance().party.remove(j);

                    for(String username : DetourManager.getInstance().party){
                        Bukkit.getPlayer(username).sendMessage(ChatColor.RED + "Игрок " + event.getPlayer().getName() + " вышел из группы");
                    }
                }
            }

        }

    }
}
