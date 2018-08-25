package ru.alphach1337.detour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;
import java.util.List;

public class Quit extends DetourCommand {
    public Quit(String name) {
        super(name);
    }

    @Override
    public String getPermission() {
        return "detour.member";
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
        return null;
    }

    @Override
    public ArrayList<String> getArgs(Player player, List<String> args) {
        return null;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        Database database = Database.getInstance();
        DetourManager detourManager = DetourManager.getInstance();

        EventParticipant participant = database.getPlayerInEvent(detourManager.getEventId(), player.getUniqueId());

        if (participant != null){
            database.removePlayerFromEvent(detourManager.getEventId(), participant);
            player.sendMessage(Settings.deletedFromList);

            return true;
        } else {
            player.sendMessage(Settings.notAdded);
            return false;
        }
    }
}
