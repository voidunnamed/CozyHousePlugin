package fr.cozyhouse.cozyHouseRPG.listeners;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.skills.items.ArcaneWand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * Single listener for PlayerInteractEvent and PlayerItemHeldEvent.
 *
 * Responsibilities:
 *   - PlayerInteractEvent  : right-click wand → toggle skill mode
 *                            left/right-click during skill mode → activate held skill
 *   - PlayerItemHeldEvent  : scroll while in skill mode → show selected skill info
 *
 * Follows the one-class-per-event-type rule. All skill mode logic is
 * delegated to SkillModeService.
 */
public class PlayerInteractListener implements Listener {

    private final PluginContext context;

    public PlayerInteractListener(PluginContext context) {
        this.context = context;
    }

    // ── Interact (click) ──────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        RPGPlayer rpg  = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null) return;

        Action action = event.getAction();
        boolean isClick =
                action == Action.RIGHT_CLICK_AIR   ||
                action == Action.RIGHT_CLICK_BLOCK  ||
                action == Action.LEFT_CLICK_AIR     ||
                action == Action.LEFT_CLICK_BLOCK;
        if (!isClick) return;

        boolean isRightClick =
                action == Action.RIGHT_CLICK_AIR ||
                action == Action.RIGHT_CLICK_BLOCK;

        // ── Wand right-click → toggle skill mode ──────────────────────────────
        if (isRightClick && ArcaneWand.isWand(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            if (!rpg.getTessereLoadout().isRevealed()) {
                player.sendMessage(context.getMessageManager().getMessageNoPrefix(
                        fr.cozyhouse.cozyHouse.messages.MessagesEnum.TESSERE.SKILL_MODE.NO_SKILLS.getPath()));
                return;
            }
            context.getSkillModeService().toggle(rpg);
            return;
        }

        // ── Any click while in skill mode → activate held skill ───────────────
        if (rpg.isInSkillMode()) {
            event.setCancelled(true);
            context.getSkillModeService().activateHeldSkill(rpg);
        }
    }

    // ── Scroll (item held change) ─────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        RPGPlayer rpg  = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null || !rpg.isInSkillMode()) return;

        // Show info about the newly selected skill after the slot changes
        context.getSkillModeService().onSkillSelected(rpg, event.getNewSlot());
    }
}
