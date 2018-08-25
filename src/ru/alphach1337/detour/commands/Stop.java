package ru.alphach1337.detour.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

import java.util.ArrayList;
import java.util.List;

public class Stop extends DetourCommand {
    public Stop(String name) {
        super(name);
    }

    @Override
    public String getPermission() {
        return "detour.manage" ;
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
        if (DetourManager.getInstance().getIsDetour()) {

            DetourManager.getInstance().stop();
            Bukkit.getServer().broadcastMessage(Settings.stopDetour);

            return true;
        } else {
            commandSender.sendMessage(Settings.notStarted);

            return false;
        }
    }
}
