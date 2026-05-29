package fr.cozyhouse.cozyHouseRPG.utils;

import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.player.experience.ExperienceService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Détecte les changements de biome pour les joueurs et affiche la barre d'XP en action bar.
 * Tous les messages proviennent du stringMessages.yml de CozyHouse via MessageManager.
 */
public class BiomeChecker {

    private static final int BAR_LENGTH = 20;

    private final Map<UUID, Biome> playerBiomes = new HashMap<>();
    private PluginContext context;

    /** Démarre la tâche de vérification (toutes les secondes). */
    public void startChecking(Plugin plugin, PluginContext context) {
        this.context = context;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                checkPlayerBiome(player);
            }
            for (RPGPlayer rpgPlayer : context.getPlayerRepository().findAll()) {
                if (rpgPlayer.getBukkitPlayer() == null || !rpgPlayer.getBukkitPlayer().isOnline()) continue;
                updateActionBar(rpgPlayer);
            }
        }, 0L, 20L);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Action bar XP
    // ─────────────────────────────────────────────────────────────────────────────

    private void updateActionBar(RPGPlayer rpgPlayer) {
        ExperienceService expSvc = rpgPlayer.getExperienceService();

        // Utilise les méthodes d'ExperienceService qui récupèrent déjà les messages
        String levelDisp = expSvc.getLevelDisplay();
        String xpText    = expSvc.getXpDisplay();
        String xpBar     = expSvc.isMaxLevel()
                ? "§6" + "█".repeat(BAR_LENGTH)
                : buildExpBar(rpgPlayer.getExperience(),
                              expSvc.calculateRequiredExperience(rpgPlayer.getLevel()),
                              BAR_LENGTH);

        String message = context.getMessageManager().getMessageNoPrefix(
                MessagesEnum.RPG.EXPERIENCE.ACTION_BAR.getPath(),
                "%level%", levelDisp,
                "%bar%",  xpBar,
                "%xp%",   xpText
        );

        rpgPlayer.getBukkitPlayer().spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(message)
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Détection de biome
    // ─────────────────────────────────────────────────────────────────────────────

    private void checkPlayerBiome(Player player) {
        UUID playerId = player.getUniqueId();
        Biome currentBiome = player.getLocation().getBlock().getBiome();
        Biome previousBiome = playerBiomes.put(playerId, currentBiome);

        if (previousBiome != null && previousBiome != currentBiome) {
            onBiomeChange(player, previousBiome, currentBiome);
        }
    }

    protected void onBiomeChange(Player player, Biome oldBiome, Biome newBiome) {
        if (context == null) return;

        // Récupération du RPGPlayer — si absent ou sans race, rien à faire
        RPGPlayer rpgPlayer = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpgPlayer == null || !rpgPlayer.hasRace()) return;

        // Message de changement de biome
        context.getMessageManager().sendNoPrefix(player,
                MessagesEnum.BIOME.CHANGED.getPath(),
                "%biome%", newBiome.toString());

        // Recalcul de la vitesse selon le biome
        double biomeMultiplier = context.getRaceRegistry()
                .getRace(rpgPlayer.getCurrentRace())
                .getBiomeSpeedMultiplier(newBiome);
        double baseSpeed = context.getRaceRegistry()
                .getRace(rpgPlayer.getCurrentRace())
                .getBaseMovementSpeed();

        rpgPlayer.setMovementSpeed(biomeMultiplier * baseSpeed);
        context.getPlayerService().refreshAttributes(rpgPlayer);
    }

    // ─────────────────────────────────────────────────────────────────────────────

    public void removePlayer(UUID playerId) {
        playerBiomes.remove(playerId);
    }

    public void clear() {
        playerBiomes.clear();
    }

    private String buildExpBar(long current, long required, int barLength) {
        int filled = required > 0 ? (int) ((double) current / required * barLength) : barLength;
        filled = Math.min(filled, barLength); // sécurité si current > required
        return "§a" + "█".repeat(filled) + "§8" + "█".repeat(barLength - filled);
    }
}
