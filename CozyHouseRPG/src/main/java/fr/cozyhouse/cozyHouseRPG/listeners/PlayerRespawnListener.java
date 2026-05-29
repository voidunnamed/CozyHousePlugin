package fr.cozyhouse.cozyHouseRPG.listeners;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Ré-applique les attributs RPG après le respawn.
 * Minecraft réinitialise certains attributs Bukkit à la mort/respawn —
 * sans ce listener, les stats de race (santé max, vitesse, armure…) seraient perdues.
 */
@RequiredArgsConstructor
public class PlayerRespawnListener implements Listener {

    private final PluginContext context;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerService playerService = context.getPlayerService();

        RPGPlayer rpgPlayer = playerService.getPlayer(player).orElse(null);
        if (rpgPlayer == null || !rpgPlayer.hasRace()) return;

        // If the player died while in skill mode, restore their inventory first.
        if (rpgPlayer.isInSkillMode()) {
            context.getSkillModeService().exitSkillMode(rpgPlayer, false);
        }

        // On attend 1 tick : Minecraft termine son propre traitement du respawn
        // avant qu'on écrase ses valeurs par défaut avec les attributs RPG.
        Bukkit.getScheduler().runTaskLater(context.getPlugin(), () -> {
            if (player.isOnline()) {
                playerService.refreshAttributes(rpgPlayer);
            }
        }, 1L);
    }
}
