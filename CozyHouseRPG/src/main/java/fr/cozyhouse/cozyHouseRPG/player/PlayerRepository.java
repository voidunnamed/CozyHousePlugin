package fr.cozyhouse.cozyHouseRPG.player;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for managing RPG player data
 * Thread-safe storage and retrieval
 * Handles player lifecycle (add, get, remove, save)
 */
public class PlayerRepository {

    private final PluginContext context;
    private final Map<UUID, RPGPlayer> players = new ConcurrentHashMap<>();

    public PlayerRepository(PluginContext context) {
        this.context = context;
    }

    /**
     * Adds or updates a player in the repository
     *
     * @param rpgPlayer the player to add
     * @return the added player
     */
    public RPGPlayer save(RPGPlayer rpgPlayer) {
        players.put(rpgPlayer.getPlayerId(), rpgPlayer);
        return rpgPlayer;
    }

    /**
     * Finds a player by UUID
     *
     * @param playerId the player's UUID
     * @return optional containing the player if found
     */
    public Optional<RPGPlayer> findById(UUID playerId) {
        return Optional.ofNullable(players.get(playerId));
    }

    /**
     * Finds a player by Bukkit player
     *
     * @param player the Bukkit player
     * @return optional containing the player if found
     */
    public Optional<RPGPlayer> findByPlayer(Player player) {
        return findById(player.getUniqueId());
    }

    /**
     * Gets a player or creates a new one if not found
     *
     * @param playerId the player's UUID
     * @return the existing or new player
     */
    public RPGPlayer getOrCreate(UUID playerId) {
        return players.computeIfAbsent(playerId, id -> new RPGPlayer(context, id));
    }

    /**
     * Removes a player from the repository
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        players.remove(playerId);
    }

    /**
     * Checks if a player exists
     *
     * @param playerId the player's UUID
     * @return true if the player exists
     */
    public boolean exists(UUID playerId) {
        return players.containsKey(playerId);
    }

    /**
     * Gets all players
     *
     * @return unmodifiable collection of all players
     */
    public Collection<RPGPlayer> findAll() {
        return Collections.unmodifiableCollection(players.values());
    }

    /**
     * Gets the number of players
     *
     * @return player count
     */
    public int count() {
        return players.size();
    }

    /**
     * Clears all players from the repository
     */
    public void clear() {
        players.clear();
    }

    /**
     * Sauvegarde tous les joueurs encore en mémoire (appelé lors du onDisable).
     * Chaque joueur est persisté via PDC + YAML si nécessaire.
     */
    public void saveAll() {
        players.values().forEach(context.getPlayerDataManager()::save);
    }
}
