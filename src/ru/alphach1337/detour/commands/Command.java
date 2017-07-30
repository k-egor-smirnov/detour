package ru.alphach1337.detour.commands;

import org.bukkit.command.CommandSender;

public interface Command {
    public String getPermission();

    public String getHelp();

    public void execute(CommandSender commandSender, org.bukkit.command.Command command, String[] args);

    default public void ExecuteConsole(CommandSender sen, org.bukkit.command.Command cmd, String[] args) {
        this.execute(sen, cmd, args);
    }
}