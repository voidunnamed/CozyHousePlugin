package fr.cozyhouse.cozyHouseRPG.listeners;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Gère la déconnexion des joueurs.
 * Sauvegarde les données (PDC + YAML) puis nettoie la mémoire.
 */
@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

    private final PluginContext context;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Sauvegarde des données avant suppression de la mémoire
        RPGPlayer rpgPlayer = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpgPlayer != null) {
            // Restore inventory silently if the player disconnects during skill mode
            if (rpgPlayer.isInSkillMode()) {
                context.getSkillModeService().exitSkillMode(rpgPlayer, false);
            }
            context.getPlayerDataManager().save(rpgPlayer);
        }

        // Retire l'entrée de biome (évite la fuite mémoire)
        context.getBiomeChecker().removePlayer(player.getUniqueId());

        // Retire le joueur du repository en mémoire
        context.getPlayerService().removePlayer(player.getUniqueId());
    }
}
