package ru.alphach1337.detour;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alphach1337.detour.commands.*;
import ru.alphach1337.detour.events.EventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class Detour extends JavaPlugin implements Listener {
    private ArrayList<Player> party = new ArrayList<>();

    private boolean isDetour = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        this.getConfig().addDefault("allowOffline", false);
        this.getConfig().addDefault("hoursToAllowDetour", 12);
        this.getConfig().options().copyDefaults(true);
        saveConfig();

        CommandHandler cw = new CommandHandler("detour");
        cw.commands.add(new Join());
        cw.commands.add(new Start());
        cw.commands.add(new Stop());
        cw.commands.add(new Next());
        cw.commands.add(new Party());
        cw.commands.add(new Stick());
        this.getCommand("detour").setExecutor(cw);
    }

    @Override
    public void onDisable() {

    }
}