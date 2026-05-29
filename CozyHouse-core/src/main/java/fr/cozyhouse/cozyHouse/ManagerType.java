package fr.cozyhouse.cozyHouse;

import fr.cozyhouse.cozyHouse.gameplayer.PlayerManager;
import fr.cozyhouse.cozyHouse.tab.TabAnimator;

import java.util.function.Consumer;

public enum ManagerType {

    PLAYER_MANAGER(plugin -> {
        PlayerManager pm = new PlayerManager();
        CozyHouseCore.getInstance().setPlayerManager(pm);
    }),

    TAB_ANIMATOR(plugin -> {
        TabAnimator ta = new TabAnimator();
        CozyHouseCore.getInstance().setTabAnimator(ta);
        ta.startAnimation();
    }),

    COMMANDS_MANAGER(CommandsManager::registerCommands),
    EVENTS_MANAGER(EventsManager::registerEvents);

    private final Consumer<CozyHouseCore> initializer;

    ManagerType(Consumer<CozyHouseCore> initializer) {
        this.initializer = initializer;
    }

    public void initialize(CozyHouseCore plugin) {
        initializer.accept(plugin);
    }
}
