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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Main extends JavaPlugin implements Listener {
    private HashMap<String, Location> loc = new HashMap<String, Location>();
    private ArrayList<String> players = new ArrayList<String>();
    private ArrayList<String> ignorePlayers = new ArrayList<String>();
    private FileConfiguration config = this.getConfig();
    private ArrayList<Player> party = new ArrayList<>();

    private boolean isDetour = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        config.addDefault("allowOffline", false);
        config.addDefault("hoursToAllowDetour", 12);
        config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (isDetour) {
            event.getPlayer().sendMessage(Settings.Started1);
            event.getPlayer().sendMessage(Settings.Started2);
        }
    }

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent event) {
        if (isDetour && config.getBoolean("allowOffline")) {

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (p.isOp() && p.getInventory().getItemInMainHand().getType() == Material.STICK && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(Settings.stick)) {
            if (event.getAction() == Action.LEFT_CLICK_AIR && players.size() >= 0) {
                if (isDetour) {
                    Player target = Bukkit.getPlayer(players.get(0));

                    if (target.isOnline()) {
                        p.teleport(target);
                    } else {
                        p.sendMessage(ChatColor.RED + "Игрок вышел из сети");
                    }
                } else {
                    p.sendMessage(Settings.notStarted);
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (isDetour) {
                    next(p);
                } else {
                    p.sendMessage(Settings.notStarted);
                }
            }
        }
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        Player p = (Player) sender;

        if (args[0].equalsIgnoreCase("join")) {
            if (p.getStatistic(Statistic.PLAY_ONE_TICK) * 50L <= (config.getInt("hoursToAllowDetour") * 60 * 60 * 1000)) {
                p.sendMessage(Settings.time + ChatColor.YELLOW + config.getInt("hoursToAllowDetour"));
                return true;
            }

            if (!isDetour) {
                p.sendMessage(Settings.notStarted);
                return true;
            }
            for (String s : loc.keySet()) {
                if (p.getName().equals(loc.get(s))) {
                    p.sendMessage(Settings.alreadyInTheList);
                    return true;
                }
            }
            if (ignorePlayers.contains(p.getName())) {
                p.sendMessage(Settings.alreadyInTheList);
                return true;
            }

            p.sendMessage(Settings.addedToList);
            Location l = p.getLocation().clone();
            loc.put(p.getName(), l.clone());
            players.add(p.getName());
            ignorePlayers.add(p.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("next")) {
            next(p);
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (!p.isOp()) {
                p.sendMessage(Settings.hasNoPermission);
                return true;
            }
            if (isDetour) {
                p.sendMessage(Settings.alreadyStarted);
                return true;
            }
            Title title = new Title(Settings.Started1, Settings.onStartSubtitle);
            title.setSubtitleColor(ChatColor.YELLOW);
            title.broadcast();
            Bukkit.broadcastMessage(Settings.Started1);
            Bukkit.broadcastMessage(Settings.Started2);
            isDetour = true;

            return true;
        }

        if (args[0].equalsIgnoreCase("stop")) {
            reset(p);
        }

        if (args[0].equalsIgnoreCase("stick")) {
            ItemStack item = new ItemStack(Material.STICK, 1);

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Settings.stick);

            item.setItemMeta(meta);

            Log.info("Палочка добавлена");
            p.getInventory().addItem(item);
        }

        return true;
    }


    private void reset(Player p) {
        if (!p.isOp()) {
            p.sendMessage(Settings.hasNoPermission);
            return;
        }
        if (!isDetour) {
            p.sendMessage(Settings.notStarted);
            return;
        }
        isDetour = false;
        players.clear();
        loc.clear();
        ignorePlayers.clear();
        Bukkit.broadcastMessage(Settings.stopDetour);
    }

    private void next(Player p) {
        if (players.size() <= 0) {
            reset(p);
            return;
        }

        boolean isOffline = false;
        if (!p.isOp()) {
            p.sendMessage(Settings.hasNoPermission);
            return;
        }
        if (!isDetour) {
            p.sendMessage(Settings.notStarted);
            return;
        }
        if (players.isEmpty()) {
            p.sendMessage(Settings.notJoined);
            return;
        }


        try {
            p.teleport(Bukkit.getPlayer(players.get(0)));
        } catch (Exception e) {
            p.teleport(loc.get(players.get(0)));
            isOffline = true;
        }


        for (int i = 0; i < players.size(); i++) {
            Player player = Bukkit.getPlayer(players.get(i));

            int position = Math.abs(players.size() - players.size() - i);

            if (player != null && player.isOnline()) {
                if (position > 0) {
                    ActionBarAPI.sendActionBar(player, ChatColor.GREEN + "Твое место в очереди " + position, 200);
                } else {
                    ActionBarAPI.sendActionBar(player, "За вами наблюдают!", 500);
                }
            }
        }


        for (String s : loc.keySet()) {
            if (players.get(0).equals(s)) {
                if (!isOffline) {
                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s);
                    p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (players.size() - 1));
                } else {
                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + s + ChatColor.RED + " (оффлайн)");
                    p.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (players.size() - 1));
                }


                players.remove(0);
                return;
            }
        }


    }
}