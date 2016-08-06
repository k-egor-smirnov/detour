package ru.alphach1337.detour;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin{
    HashMap<Location, String> loc2 = new HashMap<Location, String>();
    ArrayList<Location> loc = new ArrayList<Location>();
    ArrayList<String> ignorePlayers = new ArrayList<String>();
    public boolean isDetour = false;

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }
    public boolean onCommand(final CommandSender sender, final Command cmd,final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)){
            return false; 
        }
        if (args.length == 0){
            return false;
        }
        Player p = (Player) sender;
        if (args[0].equalsIgnoreCase("join")) {
            if (!isDetour){
                return false;
            }
            for (Location l2 : loc2.keySet()) {
                if (p.getName().equals(loc2.get(l2))) {
                    return false;
                }
            }
            if (ignorePlayers.contains(p.getName())){
                return false;
            }
            Location l = p.getLocation().clone();
            loc2.put(l.clone(), p.getName());
            loc.add(l.clone());
            return true;
        }
        if (args[0].equalsIgnoreCase("next")) {
            if (!p.isOp()){
                return false;
            }
            if (loc.isEmpty()) {
                return false;
            }
            p.teleport(loc.get(0));
            for (Location st : loc2.keySet()) {
                if (loc.get(0).equals(st)){
                    String pl = loc2.get(st);
                    loc.remove(0);
                    loc2.remove(st);
                    ignorePlayers.add(pl);
                    p.sendMessage("Добро пожаловать к игроку " + pl);
                    try {
                        Player p2 = Bukkit.getPlayer(pl);
                        p.sendMessage("Игрок " + p2.getName() + " онлайн");
                    } catch (Exception e) {
                        p.sendMessage("Игрок " + pl + " оффлайн");
                    }
                    return true;
                }
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!p.isOp()){
                return false;
            }
            if (isDetour){
                return false;
            }
            isDetour = true;
            //send msg;
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!p.isOp()){
                return false;
            }
            if (!isDetour) {
                return false;
            }
            isDetour = false;
            loc.clear();
            loc2.clear();
            ignorePlayers.clear();
            //send msg;
            return true;
        }
        return false;

    }

}