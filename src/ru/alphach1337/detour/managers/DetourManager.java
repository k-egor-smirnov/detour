package ru.alphach1337.detour.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.sqlite.DataBase;

import java.util.ArrayList;
import java.util.HashMap;

public class DetourManager {
    private static final DetourManager INSTANCE = new DetourManager();

    //public ArrayList<String> players;
    public HashMap<String, Location> locations;
    public ArrayList<String> ignorePlayers;
    public FileConfiguration config = Bukkit.getPluginManager().getPlugin("Detour").getConfig();
    public ArrayList<String> party;

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
        if(DataBase.contains(p.getUniqueId().toString(), "players") || DataBase.contains(p.getUniqueId().toString(), "ignoreplayers")){
            return false;
        }
        DataBase.insertUuid(p.getUniqueId().toString(), "players");

        DataBase.insertUuid(p.getUniqueId().toString(), "ignoreplayers");

        DataBase.insert(p.getName(), p.getUniqueId().toString(), "idandname", "name");
        Location l = p.getLocation().clone();
        DataBase.insertUuidAndLocation(p.getUniqueId().toString(), l, "locations");
        if(!DataBase.contains(p.getUniqueId().toString(), "counts")){
            DataBase.insert(""+0, p.getUniqueId().toString(), "counts", "count");
        }
        return true;
    }

    public void start() {
        isDetour = true;
    }

    public void stop() {
        deleteAllTables();
        createAllTables();
        isDetour = false;
    }

    public void createAllTables(){
        DataBase.createTable("players");
        DataBase.createTable("ignoreplayers");
        DataBase.createTable("party");
        DataBase.createDuoTable("idandname",  "name");
        DataBase.createDuoTable("locations", "location");

    }

    public void deleteAllTables(){
        DataBase.deleteTable("players");
        DataBase.deleteTable("ignoreplayers");
        DataBase.deleteTable("party");
        DataBase.deleteTable("locations");
        DataBase.deleteTable("idandname");
    }

    public static Location getLocationFromString(String locationString){
        World world;
        double[] xyz = new double[3];
        String[] locs = locationString.split("&");
        world = Bukkit.getServer().getWorld(locs[0]);
        for (int i = 1; i <= 3; i++) {
            xyz[i-1] = Double.parseDouble(locs[i]);
        }

        return new Location(world, xyz[0], xyz[1], xyz[2]);
    }
}
