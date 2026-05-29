package fr.cozyhouse.cozyHouse;

import fr.cozyhouse.cozyHouse.gameplayer.PlayerManager;
import fr.cozyhouse.cozyHouse.tab.TabAnimator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public final class CozyHouseCore extends JavaPlugin {

    @Getter
    private static CozyHouseCore instance;

    @Getter
    @Setter
    private MessageManager messageManager;

    @Getter
    @Setter
    private PlayerManager playerManager;

    @Getter
    @Setter
    private CommandsManager commandsManager;

    @Getter
    @Setter
    private TabAnimator tabAnimator;

    @Override
    public void onEnable() {
        instance = this;
        this.messageManager = new MessageManager();
        ManagerRegistry managerRegistry = new ManagerRegistry(CozyHouseCore.getInstance(), messageManager);
        managerRegistry.loadAll();
    }

    @Override
    public void onDisable() {
    }
}
