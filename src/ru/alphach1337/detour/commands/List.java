package ru.alphach1337.detour.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.sqlite.DataBase;
import java.util.ArrayList;

public class List implements Command{
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
        try {
            ArrayList<String> players = DataBase.selectAll("players");
            int count = players.size();

            for (int i = 0; i < players.size(); i++) {
                String name = DataBase.selectById(players.get(i), "idandname", "name");
                players.set(i, name);
            }

            String playersString = String.join(", ", players);
            String message = ChatColor.GREEN +  "На данный момент в обходе участвует " + ChatColor.YELLOW + count
                    + ChatColor.GREEN + " игроков: " + ChatColor.BLUE + playersString;

            Player p = (Player) commandSender;
            p.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
