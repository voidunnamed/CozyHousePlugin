package fr.cozyhouse.cozyHouseQuests.listener;

import fr.cozyhouse.cozyHouseQuests.manager.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final PlayerDataManager dataManager;

    public PlayerListener(PlayerDataManager dataManager){
        this.dataManager = dataManager;
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        dataManager.load(player.getUniqueId());
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        dataManager.unload(player.getUniqueId());
    }
}
