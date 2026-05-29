package fr.cozyhouse.cozyHouseRPG.listeners;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.gui.RaceSelectionGUI;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles player join events.
 * Initializes player data and opens race selection if needed.
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final PluginContext context;

    /**
     * Initialise le joueur à la connexion.
     * L'ouverture du GUI est retardée d'1 tick pour éviter que Minecraft
     * ne ferme l'inventaire immédiatement après le chargement du monde.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerService playerService = context.getPlayerService();

        // Initialisation des données (chargement ou création)
        RPGPlayer rpgPlayer = playerService.initializePlayer(player);

        // Ouverture du GUI de sélection si aucune race choisie
        if (!rpgPlayer.hasRace()) {
            Bukkit.getScheduler().runTaskLater(context.getPlugin(), () -> {
                if (player.isOnline()) {
                    new RaceSelectionGUI(context).open(player);
                }
            }, 1L);
        }
    }
}
