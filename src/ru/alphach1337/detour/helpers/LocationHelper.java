package ru.alphach1337.detour.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationHelper {
    public static Location deserialize(String string) {
        World world;
        double[] coordinates = new double[3];
        String[] locs = string.split("&");
        world = Bukkit.getServer().getWorld(locs[0]);
        for (int i = 1; i <= 3; i++) {
            coordinates[i - 1] = Double.parseDouble(locs[i]);
        }

        return new Location(world, coordinates[0], coordinates[1], coordinates[2]);
    }

    public static String serialize(Location location) {
        return location.getWorld().getName() + "&" + location.getX() + "&" + location.getY() + "&" + location.getZ();
    }
}
