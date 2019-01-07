package ru.alphach1337.detour.managers;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;

public class DetourManager {
    private static final DetourManager INSTANCE = new DetourManager();
    private FileConfiguration config = Bukkit.getPluginManager().getPlugin("Detour").getConfig();
    private int     eventId  = -1;

    public static DetourManager getInstance() {
        return INSTANCE;
    }

    public boolean getIsDetour() {
        return eventId != -1;
    }

    public void setEvent(int event) {
        this.eventId = event;
    }

    public int getEventId() {
        return eventId;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean addPlayer(Player player) {
        EventParticipant participant = new EventParticipant(eventId, player);

        return Database.getInstance().addPlayerInEvent(eventId, participant);
    }

    public void start() {
        eventId = Database.getInstance().startEvent();
        Bukkit.getLogger().info(new StringBuilder().append("Обход #").append(eventId).append(" создан").toString());
    }

    public void stop() {
        ArrayList<EventParticipant> participants =
                Database.getInstance().getPlayers(eventId, true, false);

        Database.getInstance().closeAllEvents();
        Bukkit.broadcastMessage(Settings.stopDetour);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Было пройдено игроков: " + ChatColor.DARK_PURPLE + participants.size());

        eventId = -1;
    }

    public void next(Player p) {
        Database database = Database.getInstance();

        try {
            if (!DetourManager.getInstance().getIsDetour()) {
                p.sendMessage(Settings.notStarted);
                return;
            }

            boolean isPlayerOffline = false;
            ArrayList<EventParticipant> participants = Database.getInstance().getPlayers(this.getEventId(), false, false);
            ArrayList<EventParticipant> party = database.getPlayers(eventId, false, true);

            Log.info(participants);

            if (participants.size() <= 0) {
                DetourManager.getInstance().stop();

                return;
            }

            if (!DetourManager.getInstance().getIsDetour()) {
                p.sendMessage(Settings.notStarted);
                return;
            }

            if (participants.isEmpty()) {
                p.sendMessage(Settings.notJoined);
                return;
            }

            try {
                for (EventParticipant participant : party) {
                    Bukkit.getPlayer(participant.getUUID()).teleport(Bukkit.getPlayer(participants.get(0).getUUID()));
                }

            } catch (Exception e) {
                for (EventParticipant participant : party) {
                    Bukkit.getPlayer(participant.getUUID()).teleport(participant.getLocation());

                }

                isPlayerOffline = true;
            }

            for (int i = 0; i < participants.size(); i++) {
                EventParticipant participant = participants.get(i);
                Player player = Bukkit.getPlayer(participant.getUUID());

                if (DetourManager.getInstance().getIsDetour() && player != null && player.isOnline()) {
                    if (i >= 1) {
                        try {
                            ActionBarAPI.sendActionBar(player, ChatColor.GREEN + "Твое место в очереди " + i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            ActionBarAPI.sendActionBar(player, ChatColor.YELLOW + "За вами наблюдают!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!isPlayerOffline) {
                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + player.getDisplayName() + ChatColor.GREEN + ". Это его " + 0 + " обход."
                            + "\n " + ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (participants.size() - 1));
                } else {

                    p.sendMessage(ChatColor.GREEN + "Добро пожаловать к игроку " + ChatColor.BLUE + player.getDisplayName() + ChatColor.RED + " (оффлайн)"
                            + ChatColor.GREEN + ". Это его " + 0 + " обход." +
                            "\n" + ChatColor.YELLOW + "Осталось: " + ChatColor.DARK_PURPLE + (participants.size() - 1));
                }

                participant.setIgnore(true);
                Database.getInstance().setPlayer(participant);

                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
