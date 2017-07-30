package ru.alphach1337.detour.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import java.util.ArrayList;

public class CommandHandler implements CommandExecutor {
    public ArrayList<Command> commands = new ArrayList<>();
    private String commandName;

    public CommandHandler(String command) {
        commandName = command;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command cmd, String s, String[] args) {
        if(args.length <= 0){
            return false;
        }

        if (cmd.getName().equals(commandName)) {
            for (Command command : commands) {

                if (command.getClass().getSimpleName().equalsIgnoreCase(args[0])) {
                    if (commandSender.hasPermission(command.getPermission())) {
                        command.execute(commandSender, cmd, args);
                    }
                }
            }

            return true;
        }else {
            return false;
        }
    }
}
