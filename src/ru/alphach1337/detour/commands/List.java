package ru.alphach1337.detour.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;
import ru.alphach1337.detour.sqlite.Database;

import java.util.ArrayList;

public class List extends DetourCommand {
    public List(String name) {
        super(name);
    }

    @Override
    public String getPermission() {
        return "detour.member" ;
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
    public ArrayList<String> getArgs(Player player, java.util.List<String> args) {
        return null;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        ArrayList<EventParticipant> players = Database.getInstance().getPlayers(
                DetourManager.getInstance().getEventId(),
                false,
                false
        );

        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            String name = Bukkit.getPlayer(players.get(i).getUUID()).getDisplayName();
            names.add(name);
        }

        String playersString = String.join(", ", names);
        String message = ChatColor.GREEN + "На данный момент в обходе участвует " + ChatColor.YELLOW + players.size()
                + ChatColor.GREEN + " игроков: " + ChatColor.BLUE + playersString;

        Player p = (Player) commandSender;
        p.sendMessage(message);

        return true;
    }
}
