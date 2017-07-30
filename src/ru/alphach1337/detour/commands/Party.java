package ru.alphach1337.detour.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Detour;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

public class Party implements Command{
    @Override
    public String getPermission() {
        return "detour.manage";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void execute(CommandSender commandSender, org.bukkit.command.Command command, String[] args) {
        if(args.length <= 2){
            return;
        }

        if(!DetourManager.getInstance().getIsDetour()){
            commandSender.sendMessage(Settings.notStarted);
            return;
        }

        switch(args[1]){
            case "add": {
                if(args.length >= 3) {
                    if(Bukkit.getPlayer(args[2]) != null) {
                        if((commandSender).getName().equalsIgnoreCase(args[2])){
                            commandSender.sendMessage(ChatColor.RED + "Нельзя добавить самого себя!");
                            return;
                        }

                        if(DetourManager.getInstance().party.contains(args[2])){
                            commandSender.sendMessage(ChatColor.RED + "Игрок уже в команде!");
                        }else {
                            DetourManager.getInstance().party.add(args[2]);
                            commandSender.sendMessage(ChatColor.GREEN + "Вы добавили игрока " + args[2] + " в команду для обхода");
                        }

                    }else{
                        commandSender.sendMessage(ChatColor.RED + "Игрок не найден");
                    }
                }else{
                    return;
                }

                break;
            }
        }
    }
}
