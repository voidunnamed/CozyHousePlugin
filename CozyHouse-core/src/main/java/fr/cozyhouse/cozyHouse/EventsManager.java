package fr.cozyhouse.cozyHouse;

import fr.cozyhouse.cozyHouse.events.PlayerJoinListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class EventsManager {

    public static void registerEvents(CozyHouseCore plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        List<Listener> listeners = new ArrayList<>();

        listeners.add(new PlayerJoinListener());

        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }
}
