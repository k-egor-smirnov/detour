package ru.alphach1337.detour;

import org.bukkit.ChatColor;

public class settings {
    public static String hasNoPermission = ChatColor.RED + "У вас нет прав для использования данной команды";
    public static String notStarted = ChatColor.RED + "Обход еще не начался!";
    public static String stopDetour = ChatColor.RED + "Обход закончен";
    public static String Started1 = ChatColor.GREEN + "Обход начался";
    public static String Started2 = ChatColor.YELLOW + "Пиши команду " + ChatColor.LIGHT_PURPLE + "/detour join" + ChatColor.YELLOW
            + ", чтобы присоединиться";
    public static String alreadyInTheList = ChatColor.RED + "Ты уже участвуешь в обходе!";
    public static String addedToList = ChatColor.GREEN + "Теперь ты участвуешь в обходе!";
    public static String notJoined = ChatColor.RED + "В очереди нет игроков!";
    public static String alreadyStarted = ChatColor.RED + "Обход уже начался!";
}
