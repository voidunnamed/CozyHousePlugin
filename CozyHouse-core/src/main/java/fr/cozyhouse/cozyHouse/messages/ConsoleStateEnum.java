package fr.cozyhouse.cozyHouse.messages;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ConsoleStateEnum {
    INFO(ChatColor.translateAlternateColorCodes('&', "&9[INFO] &r")),
    SEVERE(ChatColor.translateAlternateColorCodes('&', "&c[SEVERE] &r")),
    WARNING(ChatColor.translateAlternateColorCodes('&', "&e[WARNING] &r")),
    DEBUG(ChatColor.translateAlternateColorCodes('&', "&d[DEBUG] &r"));

    public final String prefix;
}
