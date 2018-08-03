package ru.alphach1337.detour.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;

public class Stick implements Command{
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
        if(DetourManager.getInstance().getIsDetour()){
            ItemStack item = new ItemStack(Material.STICK, 1);

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Settings.stick);

            item.setItemMeta(meta);

            ((Player) commandSender).getInventory().addItem(item);
        }else{
            commandSender.sendMessage(Settings.notStarted);
        }
    }
}
