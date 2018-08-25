package ru.alphach1337.detour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class DetourCommand extends Command {
    protected DetourCommand(String name) {
        super(name);

        this.setDescription(this.getDescription());
        this.setUsage(this.getUsage());
    }

    public abstract String getPermission();

    public abstract String getDescription();

    public abstract String getUsage();

    public abstract String getHelp();

    // Return null will show players list so you don't need to handle this case
    public abstract ArrayList<String> getArgs(Player player, List<String> args);
}