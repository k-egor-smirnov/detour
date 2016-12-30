package ru.alphach1337.detour;

import com.mojang.authlib.GameProfile;
import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.HologramLine;
import com.sainttx.holograms.api.line.TextLine;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.MinecraftServer;
import net.minecraft.server.v1_11_R1.PlayerInteractManager;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class Main extends JavaPlugin implements Listener {
    HashMap<String, Location> loc = new HashMap<String, Location>();
    ArrayList<String> players = new ArrayList<String>();
    ArrayList<String> ignorePlayers = new ArrayList<String>();
    HashMap<String, Hologram> playersHolograms = new HashMap<String, Hologram>();

    public boolean isDetour = false;
    private HologramManager hologramManager;
    private Integer lastHologramId = 0;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (isDetour) {
            event.getPlayer().sendMessage(settings.Started1);
            event.getPlayer().sendMessage(settings.Started2);
        }
    }

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent event) {
        if (isDetour) {
            MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
            EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(UUID.fromString("c793afb5-c4b7-4fdb-a100-b761315913c4"), "PogoStick29"), new PlayerInteractManager(nmsWorld));
            Player p = event.getPlayer();
            npc.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 0, 0);
        }
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
        }
        if (args.length == 0) {
            return false;
        }

        Player p = (Player) sender;
        if (args[0].equalsIgnoreCase("join")) {
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            for (String s : loc.keySet()) {
                if (p.getName().equals(loc.get(s))) {
                    p.sendMessage(settings.alreadyInTheList);
                    return true;
                }
            }
            if (ignorePlayers.contains(p.getName())) {
                p.sendMessage(settings.alreadyInTheList);
                return true;
            }

            p.sendMessage(settings.addedToList);
            Location l = p.getLocation().clone();
            loc.put(p.getName(), l.clone());
            players.add(p.getName());
            ignorePlayers.add(p.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("next")) {
            boolean isOffline = false;
            if (!p.isOp()) {
                p.sendMessage(settings.hasNoPermission);
                return true;
            }
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            if (players.isEmpty()) {
                p.sendMessage(settings.notJoined);
                return true;
            }

            try {
                p.teleport(Bukkit.getPlayer(players.get(0)));
            } catch (Exception e) {
                p.teleport(loc.get(players.get(0)));
                isOffline = true;
            }

            for (String s : loc.keySet()) {
                if (players.get(0).equals(s)) {
                    players.remove(0);
                    loc.remove(s);
                    if (!isOffline) {
                        p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s);
                        p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + players.size());
                    } else {
                        p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s + ChatColor.RED + " (оффлайн)");
                        p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + players.size());
                    }
                    return true;
                }
            }
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!p.isOp()) {
                p.sendMessage(settings.hasNoPermission);
                return true;
            }
            if (isDetour) {
                p.sendMessage(settings.alreadyStarted);
                return true;
            }
            Title title = new Title(settings.Started1, settings.onStartSubtitle);
            title.setSubtitleColor(ChatColor.YELLOW);
            title.broadcast();
            Bukkit.broadcastMessage(settings.Started1);
            Bukkit.broadcastMessage(settings.Started2);
            isDetour = true;
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!p.isOp()) {
                p.sendMessage(settings.hasNoPermission);
                return true;
            }
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            isDetour = false;
            players.clear();
            loc.clear();
            ignorePlayers.clear();
            Bukkit.broadcastMessage(settings.stopDetour);
            return true;

        }

        if (args[0].equalsIgnoreCase("add-table")) {
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
                return true;
            }
            for (String uuid : playersHolograms.keySet()) {
                System.out.println(uuid);
                System.out.println(p.getUniqueId().toString());
                if (uuid.equalsIgnoreCase(p.getUniqueId().toString())) {
                    p.sendMessage(Color.RED + "Ты уже поставил голограмму ¯\\_(ツ)_/¯");
                    return true;
                }
            }
            lastHologramId++;
            Hologram hologram = new Hologram(p.getUniqueId().toString(), p.getLocation().add(0, 2, 0));
            hologramManager.addActiveHologram(hologram);
            HologramLine line = new TextLine(hologram, Color.GREEN + "Табличка игрока " + p.getName());
            hologram.addLine(line);
        }

        if (args[0].equalsIgnoreCase("add-string")) {
            if (!isDetour) {
                p.sendMessage(settings.notStarted);
            }
            String string = "";
            for (int i = 1; i <= args.length - 1; i++) {
                string += " " + args[i];
            }

            if (string.length() > 15) {
                p.sendMessage(settings.tooManySymbols);
                p.sendMessage(settings.sybmolsHint);
            } else {
                Hologram hologram;
                try {
                    hologram = hologramManager.getHologram(p.getUniqueId().toString());
                } catch (Exception e) {
                    p.sendMessage(Color.RED + "Вы еще не создали табличку");
                    return true;
                }
                HologramLine line = new TextLine(hologram, string);
                if (hologram.getLines().size() > 9) {
                    p.sendMessage(Color.RED + "Больше 9 строк!");
                } else {
                    hologram.addLine(line);
                }
            }
        }

        //TODO: NPC npc = new NPC("My npc!", new Location(Bukkit.getWorld("world"), 123, 64, 456);

        return true;
    }

}