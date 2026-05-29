package fr.cozyhouse.cozyHouse.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class ConverterUtils {

    public static String stringLocation(Location loc) {
        if (loc == null) {
            return "null";
        }
        return loc.getX() + " " + loc.getY() + " " + loc.getZ();
    }

    public static Location locationString(String locString) {
        String[] parts = locString.split(" ");
        if (parts.length != 3) {
            return null;
        }

        // Récupère le monde directement depuis l'index 0
        World world = Bukkit.getWorlds().getFirst();

        try {
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Long secondesToTicks(Double number) {
        return (long) (number * 20);
    }

    public static Long secondesToTicks(Integer number) {
        return (long) (number * 20);
    }

    public static Integer secondesToTicksInt(Integer number, Integer afterDot) {
        return (number * 20) + afterDot;
    }

    public static Boolean checkStringToInt(String message) {
        try {
            Integer.valueOf(message);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int convertStringToInt(String message) {
        return checkStringToInt(message) ? Integer.parseInt(message) : 0; // Renvoie 0 au lieu de null
    }

    public static String convertChatColor(String message) {
        if (message == null) {
            return "";
        }
        return message.replace('&', '§');
    }
}
