package com.connorlinfoot.actionbarapi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ActionBarAPI extends JavaPlugin implements Listener {
    public static Plugin plugin;
    public static boolean works = true;
    public static String nmsver;
    public static boolean useOldMethods = false;

    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        CLUpdate clUpdate = new CLUpdate(this);

        Server server = getServer();
        ConsoleCommandSender console = server.getConsoleSender();

        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
        if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.startsWith("v1_7_")) { // Not sure if 1_7 works for the protocol hack?
            useOldMethods = true;
        }

        console.sendMessage(ChatColor.AQUA + getDescription().getName() + " V" + getDescription().getVersion() + " has been enabled!");
        Bukkit.getPluginManager().registerEvents(clUpdate, this);
    }

    public static void sendActionBar(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }
        // Call the event, if cancelled don't send Action Bar
        ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
        Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
        if (actionBarMessageEvent.isCancelled())
            return;
        sendActionBarPost112(player, message);
    }

    private static void sendActionBarPost112(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        try {
            String nmsver = Bukkit.getServer().getClass().getPackage().getName();
            nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
            Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
            Object chatMessageType = null;
            for (Object obj : chatMessageTypes) {
                if (obj.toString().equals("GAME_INFO")) {
                    chatMessageType = obj;
                }
            }
            Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
            ppoc = c4.getConstructor(new Class<?>[]{c3, chatMessageTypeClass}).newInstance(o, chatMessageType);
            Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
            Object h = m1.invoke(craftPlayer);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
            works = false;
        }
    }
}