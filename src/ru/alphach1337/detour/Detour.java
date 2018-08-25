package ru.alphach1337.detour;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alphach1337.detour.commands.*;
import ru.alphach1337.detour.events.EventListener;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.sqlite.Database;

public class Detour extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        this.getConfig().addDefault("allowOffline", true);
        this.getConfig().addDefault("hoursToAllowDetour", 12);
        this.getConfig().addDefault("partyGameMode", String.valueOf(GameMode.SPECTATOR));
        this.getConfig().options().copyDefaults(true);
        saveConfig();

        CommandHandler cw = new CommandHandler("detour");

        cw.commands.add(new Join("join"));
        cw.commands.add(new Quit("quit"));
        cw.commands.add(new List("list"));
        cw.commands.add(new Start("start"));
        cw.commands.add(new Stop("stop"));
        cw.commands.add(new Next("next"));
        cw.commands.add(new Party("party"));
        cw.commands.add(new Stick("stick"));

        this.getCommand("detour").setExecutor(cw);

        DetourManager.getInstance().setEvent(Database.getInstance().init());
    }

    @Override
    public void onDisable() {

    }
}