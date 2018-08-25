package ru.alphach1337.detour.commands;

import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements TabCompleter, CommandExecutor {
    public ArrayList<DetourCommand> commands = new ArrayList<>();
    private String commandName;

    public CommandHandler(String command) {
        commandName = command;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length <= 0) {
            return false;
        }

        if (cmd.getName().equals(commandName)) {
            for (DetourCommand command : commands) {

                if (command.getClass().getSimpleName().equalsIgnoreCase(args[0])) {
                    if (commandSender.hasPermission(command.getPermission())) {
                        command.execute(commandSender, cmd.toString(), args);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length <= 1) {
            ArrayList<String> strings = new ArrayList<>();

            for (DetourCommand command : commands) {
                if (
                        command.getClass().getSimpleName().toLowerCase().startsWith(args[0]) &&
                        commandSender.hasPermission(command.getPermission())
                ) {
                    strings.add(command.getClass().getSimpleName().toLowerCase());
                }
            }

            return strings;
        }

        if (cmd.getName().equals(commandName)) {
            for (DetourCommand command : commands) {
                if (command.getClass().getSimpleName().equalsIgnoreCase(args[0])) {
                    List<String> curArgs = Arrays.asList(args);
                    curArgs.subList(2, curArgs.size());

                    return command.getArgs((Player) commandSender, curArgs);
                }
            }
        }

        return null;
    }
}
