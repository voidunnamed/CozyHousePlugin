package fr.cozyhouse.cozyHouse;

import fr.cozyhouse.cozyHouse.commands.AdminCommands;

import java.util.Objects;

public class CommandsManager {

    public static void registerCommands(CozyHouseCore plugin) {
        Objects.requireNonNull(plugin.getCommand("cozyhouse")).setExecutor(new AdminCommands());
    }
}
