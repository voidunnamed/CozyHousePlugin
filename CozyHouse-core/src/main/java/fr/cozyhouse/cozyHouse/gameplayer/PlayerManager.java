package fr.cozyhouse.cozyHouse.gameplayer;

import fr.cozyhouse.cozyHouse.CozyHouseCore;
import fr.cozyhouse.cozyHouse.MessageManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final CozyHouseCore plugin;

    private final MessageManager messageManager;

    private final Map<UUID, GamePlayer> players;


    public PlayerManager() {
        this.plugin = CozyHouseCore.getInstance();
        this.messageManager = CozyHouseCore.getInstance().getMessageManager();
        this.players = new HashMap<>();
    }

    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        GamePlayer gamePlayer = getPlayer(player);

        if (gamePlayer != null) {
            return;
        }

        gamePlayer = new GamePlayer(uuid);
        players.put(uuid, gamePlayer);
    }

    public GamePlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public GamePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
}
