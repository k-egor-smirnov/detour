package ru.alphach1337.detour.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

public class Stop implements Command{
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
        if(DetourManager.getInstance().getIsDetour()) {

            DetourManager.getInstance().stop();
            Bukkit.getServer().broadcastMessage(Settings.stopDetour);
        }else{
            commandSender.sendMessage(Settings.notStarted);
        }
    }
}
