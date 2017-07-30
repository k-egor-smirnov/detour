package ru.alphach1337.detour.managers;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Detour;
import ru.alphach1337.detour.Settings;

import java.util.ArrayList;
import java.util.HashMap;

public class DetourManager {
    private static final DetourManager INSTANCE = new DetourManager();

    public ArrayList<String> players = new ArrayList<String>();
    public HashMap<String, Location> locations = new HashMap<String, Location>();
    public ArrayList<String> ignorePlayers = new ArrayList<String>();
    public FileConfiguration config = Bukkit.getPluginManager().getPlugin("Detour").getConfig();
    public ArrayList<String> party = new ArrayList<>();

    private boolean isDetour = false;

    public static DetourManager getInstance() {
        return INSTANCE;
    }

    private DetourManager() {
    }

    public boolean getIsDetour() {
        return isDetour;
    }

    public boolean addPlayer(Player p){
        if(players.contains(p.getName()) || ignorePlayers.contains(p.getName())){
            return false;
        }
        players.add(p.getName());

        ignorePlayers.add(p.getName());

        Location l = p.getLocation().clone();
        locations.put(p.getName(), l.clone());

        return true;
    }

    public void start() {
        isDetour = true;
    }

    public void stop() {
        reset();
        isDetour = false;
    }

    public void reset(){
        players.clear();
        locations.clear();
        party.clear();
        ignorePlayers.clear();
    }
}
