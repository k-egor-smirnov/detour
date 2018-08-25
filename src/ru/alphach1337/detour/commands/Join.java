package ru.alphach1337.detour.commands;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

import java.util.ArrayList;
import java.util.List;

public class Join extends DetourCommand {
    public Join(String name) {
        super(name);
    }

    @Override
    public String getPermission() {
        return "detour.member";
    }

    @Override
    public String getDescription() {
        return "Присоединяет к обходу";
    }

    @Override
    public String getUsage() {
        return "/detour join";
    }

    @Override
    public String getHelp() {
        return this.getDescription();
    }

    @Override
    public ArrayList<String> getArgs(Player player, List<String> args) {
        return null;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        DetourManager detourManager = DetourManager.getInstance();

        Player player = (Player) commandSender;
        FileConfiguration config = detourManager.getConfig();

        if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L <= (config.getInt("hoursToAllowDetour") * 60 * 60 * 1000)) {
            player.sendMessage(Settings.time + ChatColor.YELLOW + config.getInt("hoursToAllowDetour"));
        } else {
            if (!detourManager.getIsDetour() && !config.getBoolean("allowOffline")) {
                player.sendMessage(Settings.notStarted);
                return false;
            }

            if (detourManager.addPlayer(player)) {
                player.sendMessage(Settings.addedToList);

                return true;
            } else {
                player.sendMessage(Settings.alreadyInTheList);
                return false;
            }
        }

        return false;
    }
}
