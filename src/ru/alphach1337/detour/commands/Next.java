package ru.alphach1337.detour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;

import java.util.ArrayList;
import java.util.List;

public class Next extends DetourCommand {
    public Next(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        DetourManager.getInstance().next((Player) commandSender);

        return true;
    }

    @Override
    public String getPermission() {
        return "detour.manage";
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
        return null;
    }


    private ArrayList<EventParticipant> getOwnerParty(ArrayList<EventParticipant> participants) {
        ArrayList<EventParticipant> party = new ArrayList<>();

        for (EventParticipant participant : participants) {
            if (participant.getIsReviewer()) {
                party.add(participant);
            }
        }

        return party;
    }
}
