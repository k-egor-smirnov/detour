package ru.alphach1337.detour.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;
import java.util.List;

public class Party extends DetourCommand {
    public Party(String name) {
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
        ArrayList<String> strings = new ArrayList<>();

        if (args.size() <= 2) {
            strings.add("add");

            return strings;
        }

        if (args.size() >= 4) {
            return new ArrayList<>();
        }

        return null;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length <= 2) {
            return false;
        }

        if (!DetourManager.getInstance().getIsDetour()) {
            commandSender.sendMessage(Settings.notStarted);
            return false;
        }

        switch (args[1]) {
            case "add": {
                Player player = Bukkit.getPlayer(args[2]);

                if (player != null) {
                    if (commandSender.getName().equalsIgnoreCase(player.getName())) {
                        commandSender.sendMessage(ChatColor.RED + "Нельзя добавить самого себя!");
                        return false;
                    }

                    EventParticipant participant =
                            Database.getInstance().getPlayerInEvent(
                                    DetourManager.getInstance().getEventId(),
                                    player.getUniqueId()
                            );

                    if (participant == null) {
                        participant = new EventParticipant(DetourManager.getInstance().getEventId(), player);
                    }

                    if (participant.getIsReviewer()) {
                        commandSender.sendMessage(ChatColor.RED + "Игрок уже в команде!");
                    } else {
                        participant.setIsReviewer(true);
                        Database.getInstance().setPlayer(participant);

                        commandSender.sendMessage(ChatColor.GREEN + "Вы добавили игрока " + args[2] + " в команду для обхода");
                        player.sendMessage(ChatColor.GREEN + "Вы добавлены в команду для обхода");
                        player.setGameMode(GameMode.SPECTATOR);
                    }

                } else {
                    commandSender.sendMessage(ChatColor.RED + "Игрок не найден");
                }

                break;
            }
        }

        return false;
    }
}
