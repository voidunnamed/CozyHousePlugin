package fr.cozyhouse.cozyHouseRPG.listeners;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Single listener for all damage-related events.
 *
 * Handles all combat effects linked to Tesseres:
 *   - EntityDamageByEntityEvent : offensive effects (MARQUE_ARDENTE, IMPACT_CINETIQUE, etc.)
 *   - EntityDamageEvent         : defensive effects (BOUCLIER_LUMINE)
 *
 * Passive stat effects (armor, max HP, etc.) are applied directly by
 * TessereService.appliquerEffetsPassifs() on reveal/login.
 *
 * To add a new combat effect: add a case to onEntityDamageByEntity()
 * or onEntityDamage() depending on whether it is offensive or defensive.
 */
public class EntityDamageListener implements Listener {

    private final PluginContext context;

    public EntityDamageListener(PluginContext context) {
        this.context = context;
    }

    // ── Offensive effects (player attacks) ────────────────────────────────────

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        RPGPlayer rpg = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null || !rpg.getTessereLoadout().isRevealed()) return;

        double score = rpg.getTessereLoadout().getEfficaciteScore();
        MessageManager msg = context.getMessageManager();

        for (Skill skill : rpg.getTessereLoadout().getActiveSkills()) {
            if (!(skill instanceof TessereSkill ts)) continue;

            switch (ts) {

                // Chance to set target on fire
                case MARQUE_ARDENTE -> {
                    if (Math.random() < ts.param("chance", score))
                        event.getEntity().setFireTicks((int) ts.param("duree_ticks", score));
                }

                // Amplified knockback
                case IMPACT_CINETIQUE -> {
                    if (event.getEntity() instanceof LivingEntity target) {
                        double power = ts.param("puissance", score);
                        target.setVelocity(player.getLocation().getDirection()
                                .multiply(power).setY(0.2));
                    }
                }

                // Bonus damage while sneaking
                case LAME_DU_VIDE -> {
                    if (player.isSneaking())
                        event.setDamage(event.getDamage() * (1 + ts.param("bonus", score)));
                }

                // Apply Weakness to target
                case DECLIN_FORCE -> {
                    if (event.getEntity() instanceof LivingEntity target)
                        target.addPotionEffect(new PotionEffect(
                                PotionEffectType.WEAKNESS,
                                (int) ts.param("duree_ticks", score), 0));
                }

                // Random damage multiplier
                case CHAOS_LATENT -> {
                    if (Math.random() < ts.param("chance", score)) {
                        double multMin = ts.paramBrut("mult_min");
                        double multMax = ts.paramBrut("mult_max");
                        double mult    = multMin + Math.random() * (multMax - multMin);
                        event.setDamage(event.getDamage() * mult);
                        player.sendMessage(mult > 1.0
                                ? msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMBAT.CHAOS_SURGE.getPath())
                                : msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMBAT.CHAOS_DISSIPATION.getPath()));
                    }
                }

                default -> { /* no offensive effect for this skill */ }
            }
        }
    }

    // ── Defensive effects (player receives damage) ────────────────────────────

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        // Skip EntityDamageByEntityEvent — already handled above
        if (event instanceof EntityDamageByEntityEvent) return;
        if (!(event.getEntity() instanceof Player player)) return;

        RPGPlayer rpg = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null || !rpg.getTessereLoadout().isRevealed()) return;

        double score = rpg.getTessereLoadout().getEfficaciteScore();

        for (Skill skill : rpg.getTessereLoadout().getActiveSkills()) {
            if (!(skill instanceof TessereSkill ts)) continue;

            switch (ts) {

                // Reduce magic damage
                case BOUCLIER_LUMINE -> {
                    if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC)
                        event.setDamage(event.getDamage() * (1 - ts.param("reduction", score)));
                }

                default -> { /* no defensive effect for this skill */ }
            }
        }
    }
}
