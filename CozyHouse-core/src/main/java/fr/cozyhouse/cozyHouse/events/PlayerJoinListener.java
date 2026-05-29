package fr.cozyhouse.cozyHouse.events;

import fr.cozyhouse.cozyHouse.CozyHouseCore;
import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.gameplayer.GamePlayer;
import fr.cozyhouse.cozyHouse.gameplayer.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final MessageManager messageManager;

    private final PlayerManager playerManager;

    public PlayerJoinListener() {
        this.messageManager = CozyHouseCore.getInstance().getMessageManager();
        this.playerManager = CozyHouseCore.getInstance().getPlayerManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        GamePlayer gamePlayer = initializeGamePlayer(player);
    }

    private GamePlayer initializeGamePlayer(Player player) {
        playerManager.addPlayer(player);
        GamePlayer gamePlayer = playerManager.getPlayer(player);
        gamePlayer.setPlayer(player);
        gamePlayer.initializeMoney();
        return gamePlayer;
    }
}
