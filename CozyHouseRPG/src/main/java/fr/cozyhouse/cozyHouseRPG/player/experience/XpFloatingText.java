package fr.cozyhouse.cozyHouseRPG.player.experience;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * Affiche un texte flottant "+X EXP" visible uniquement par le joueur ciblé.
 * Fonctionnement :
 *  1. Un ArmorStand est spawné dans le monde (nécessaire pour avoir un entityId valide).
 *  2. Dans le même tick, on envoie un packet de suppression à tous les autres joueurs
 *     → le client ne rend jamais l'entité pour eux (spawn + remove arrivent groupés).
 *  3. Après DISPLAY_TICKS, l'ArmorStand est supprimé du monde.
 */
public final class XpFloatingText {

    /** Durée d'affichage (en ticks). 50 ticks = 2.5 secondes. */
    private static final long DISPLAY_TICKS = 50L;

    /** Hauteur au-dessus de la position de mort du mob. */
    private static final double Y_OFFSET = 1.8;

    private XpFloatingText() {}

    /**
     * Fait apparaître un texte flottant "+xpAmount EXP" visible uniquement par {@code player}.
     *
     * @param plugin    instance du plugin (pour le scheduler Bukkit)
     * @param player    joueur qui doit voir le texte
     * @param location  position du mob mort
     * @param xpAmount  quantité d'XP finale (après bonus) à afficher
     */
    public static void spawn(Plugin plugin, Player player, Location location, long xpAmount) {
        Location spawnLoc = location.clone().add(0, Y_OFFSET, 0);

        // ── Spawn de l'ArmorStand ─────────────────────────────────────────────
        ArmorStand stand = (ArmorStand) Objects.requireNonNull(spawnLoc.getWorld()).spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        stand.setCustomName("§a+" + xpAmount + " §6EXP");
        stand.setCustomNameVisible(true);
        stand.setVisible(false);       // corps invisible
        stand.setGravity(false);       // ne tombe pas
        stand.setMarker(true);         // pas de hitbox, pas de collision
        stand.setSmall(true);          // réduit le volume de l'entité
        stand.setInvulnerable(true);

        // ── Masquer à tous les autres joueurs dans le même tick ───────────────
        // hideEntity() envoie un Remove packet immédiatement, groupé avec le
        // Spawn packet → le client reçoit les deux ensemble et n'affiche rien.
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.getUniqueId().equals(player.getUniqueId())) {
                other.hideEntity(plugin, stand);
            }
        }

        // ── Suppression après le délai ────────────────────────────────────────
        final ArmorStand ref = stand;
        new BukkitRunnable() {
            @Override
            public void run() {
                ref.remove();
            }
        }.runTaskLater(plugin, DISPLAY_TICKS);
    }
}
