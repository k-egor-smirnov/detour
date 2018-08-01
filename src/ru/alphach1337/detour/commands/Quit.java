package ru.alphach1337.detour.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.sqlite.DataBase;

public class Quit implements Command {
    @Override
    public String getPermission() {
        return "detour.member";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void execute(CommandSender commandSender, org.bukkit.command.Command command, String[] args) {
        Player player = (Player) commandSender;

        if(DataBase.contains(player.getUniqueId().toString(), "players")){
            DataBase.delete(player.getUniqueId().toString(), "players");
            DataBase.delete(player.getUniqueId().toString(), "ignoreplayers");
            DataBase.delete(player.getUniqueId().toString(), "locations");
            player.sendMessage(Settings.deletedFromList);
        }else{
            player.sendMessage(Settings.notAdded);
        }
    }
}
