package ru.alphach1337.detour;


import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alphach1337.detour.commands.*;
import ru.alphach1337.detour.events.EventListener;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.sqlite.DataBase;

public class Detour extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        this.getConfig().addDefault("allowOffline", true);
        this.getConfig().addDefault("hoursToAllowDetour", 12);
        this.getConfig().options().copyDefaults(true);
        saveConfig();

        CommandHandler cw = new CommandHandler("detour");
        cw.commands.add(new Join());
        cw.commands.add(new Quit());
        cw.commands.add(new List());
        cw.commands.add(new Start());
        cw.commands.add(new Stop());
        cw.commands.add(new Next());
        cw.commands.add(new Party());
        cw.commands.add(new Stick());

        this.getCommand("detour").setExecutor(cw);

        DetourManager.getInstance().createAllTables();
        DataBase.createDuoTable("counts", "count");
    }

    @Override
    public void onDisable() {

    }
}