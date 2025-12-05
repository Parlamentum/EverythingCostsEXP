package com.parlamentum.everythingcostsexp.util;

import org.bukkit.ChatColor;

public class MessageUtil {

    public static String colorize(String message) {
        if (message == null)
            return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
