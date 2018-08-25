package ru.alphach1337.detour.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.Title;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;
import java.util.List;

public class Start extends DetourCommand {
    public Start(String name) {
        super(name);
    }

    public String getPermission() {
        return "detour.manage";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getHelp() {
        return "Тебе ничего не поможет";
    }

    @Override
    public ArrayList<String> getArgs(Player player, List<String> args) {
        return null;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        DetourManager detourManager = DetourManager.getInstance();

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Только игроки могут использовать эту команду!");
            return false;
        }

        if (!detourManager.getIsDetour()) {
            detourManager.start();

            EventParticipant eventCreator = new EventParticipant(
                    ((Player) commandSender).getUniqueId(),
                    detourManager.getEventId(),
                    ((Player) commandSender).getLocation(),
                    true,
                    false
            );

            Database.getInstance().addPlayerInEvent(detourManager.getEventId(), eventCreator);

            Title title = new Title(Settings.Started1, Settings.onStartSubtitle);
            title.setSubtitleColor(ChatColor.YELLOW);
            title.broadcast();
            Bukkit.broadcastMessage(Settings.Started1);
            Bukkit.broadcastMessage(Settings.Started2);

            commandSender.sendMessage(ChatColor.YELLOW + "Вы можете проводить совместные обходы с помощью команды\n" +
                    ChatColor.DARK_AQUA + "/detour " + ChatColor.BLUE + "party add " + ChatColor.WHITE + "<username>");

            return true;
        } else {
            commandSender.sendMessage(Settings.alreadyStarted);
            return false;
        }
    }
}
