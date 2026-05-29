package fr.cozyhouse.cozyHouseRPG.skills.logic;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.SkillType;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSkill;
import fr.cozyhouse.cozyHouseRPG.skills.items.ArcaneWand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the skill activation mode.
 *
 * Flow:
 *   1. Player right-clicks the Arcane Wand
 *      → inventory saved, cleared, 3 skill items placed in hotbar slots 0-2
 *      → wand placed in slot 3 as cancel button
 *   2. Player scrolls to select a skill (PlayerItemHeldEvent shows selection info)
 *   3. Player left- or right-clicks → skill activated → normal mode restored
 *   4. Alternatively, player right-clicks the wand again → cancel, normal mode restored
 *
 * State is stored directly in RPGPlayer (inSkillMode + savedInventory).
 */
public class SkillModeService {

    /** PDC key on hotbar skill items — stores the skill index (0, 1, 2). */
    public static final NamespacedKey KEY_SKILL_INDEX =
            new NamespacedKey("cozyhouserpg", "skill_mode_index");

    private final PluginContext context;

    public SkillModeService(PluginContext context) {
        this.context = context;
    }

    // ── Entry / exit ──────────────────────────────────────────────────────────

    /**
     * Toggles skill mode for the given player.
     * If already in skill mode, exits; otherwise enters.
     */
    public void toggle(RPGPlayer rpg) {
        if (rpg.isInSkillMode()) {
            exitSkillMode(rpg, true);
        } else {
            enterSkillMode(rpg);
        }
    }

    /**
     * Enters skill mode:
     *   - Saves the full inventory (36 storage slots)
     *   - Clears the inventory
     *   - Places the 3 skill items in hotbar slots 0-2
     *   - Places the wand in slot 3 (cancel button)
     *   - Moves focus to slot 0
     */
    public void enterSkillMode(RPGPlayer rpg) {
        MessageManager msg = context.getMessageManager();
        Player player = rpg.getBukkitPlayer();

        List<Skill> skills = rpg.getTessereLoadout().getActiveSkills();
        if (skills.isEmpty()) {
            player.sendMessage(msg.getMessageNoPrefix(
                    MessagesEnum.TESSERE.SKILL_MODE.NO_SKILLS.getPath()));
            return;
        }

        // Save all 36 inventory storage slots (not armor/offhand)
        rpg.setSavedInventory(player.getInventory().getStorageContents().clone());

        // Clear inventory
        player.getInventory().clear();

        // Place skill items in hotbar slots 0, 1, 2
        for (int i = 0; i < Math.min(3, skills.size()); i++) {
            player.getInventory().setItem(i, buildSkillItem(skills.get(i), i, msg));
        }

        // Place wand in slot 3 as cancel indicator
        ItemStack wand = ArcaneWand.build();
        ItemMeta wandMeta = wand.getItemMeta();
        if (wandMeta != null) {
            wandMeta.setLore(List.of(
                    ChatColor.GRAY + "Channels your engraved Tessere power.",
                    "",
                    ChatColor.DARK_GRAY + "Right-click to cancel Skill Mode."
            ));
            wand.setItemMeta(wandMeta);
        }
        player.getInventory().setItem(3, wand);

        // Focus slot 0
        player.getInventory().setHeldItemSlot(0);
        rpg.setInSkillMode(true);

        player.sendMessage(msg.getMessageNoPrefix(
                MessagesEnum.TESSERE.SKILL_MODE.ENTER.getPath()));
    }

    /**
     * Exits skill mode and restores the saved inventory.
     *
     * @param sendMessage whether to send the exit message to the player
     */
    public void exitSkillMode(RPGPlayer rpg, boolean sendMessage) {
        if (!rpg.isInSkillMode()) return;
        Player player = rpg.getBukkitPlayer();

        // Clear the skill hotbar
        player.getInventory().clear();

        // Restore saved inventory
        if (rpg.getSavedInventory() != null) {
            player.getInventory().setStorageContents(rpg.getSavedInventory());
            rpg.setSavedInventory(null);
        }

        rpg.setInSkillMode(false);

        if (sendMessage && player.isOnline()) {
            player.sendMessage(context.getMessageManager().getMessageNoPrefix(
                    MessagesEnum.TESSERE.SKILL_MODE.EXIT.getPath()));
        }
    }

    // ── Skill selection info (called by PlayerItemHeldEvent) ─────────────────

    /**
     * Sends info about the currently selected skill item to the player.
     * Called whenever the player scrolls in skill mode.
     */
    public void onSkillSelected(RPGPlayer rpg, int newSlot) {
        Player player = rpg.getBukkitPlayer();
        ItemStack heldItem = player.getInventory().getItem(newSlot);
        if (heldItem == null || !heldItem.hasItemMeta()) return;

        Integer index = heldItem.getItemMeta()
                .getPersistentDataContainer()
                .get(KEY_SKILL_INDEX, PersistentDataType.INTEGER);
        if (index == null) return; // wand or empty

        List<Skill> skills = rpg.getTessereLoadout().getActiveSkills();
        if (index < 0 || index >= skills.size()) return;

        Skill skill = skills.get(index);
        player.sendMessage(context.getMessageManager().getMessageNoPrefix(
                MessagesEnum.TESSERE.SKILL_MODE.SELECTED.getPath(),
                "%skill%",       skill.getDisplayName(),
                "%description%", skill.getDescription()));
    }

    // ── Skill activation ─────────────────────────────────────────────────────

    /**
     * Activates the skill currently held by the player.
     * Exits skill mode after activation regardless of outcome.
     */
    public void activateHeldSkill(RPGPlayer rpg) {
        Player player = rpg.getBukkitPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();

        if (!held.hasItemMeta()) {
            exitSkillMode(rpg, true);
            return;
        }

        Integer index = held.getItemMeta()
                .getPersistentDataContainer()
                .get(KEY_SKILL_INDEX, PersistentDataType.INTEGER);

        if (index == null) {
            // Clicked the wand → cancel
            exitSkillMode(rpg, true);
            return;
        }

        List<Skill> skills = rpg.getTessereLoadout().getActiveSkills();
        if (index < 0 || index >= skills.size()) {
            exitSkillMode(rpg, true);
            return;
        }

        Skill skill = skills.get(index);
        MessageManager msg = context.getMessageManager();

        // Exit skill mode FIRST so activation effects feel natural
        exitSkillMode(rpg, false);

        // Execute the skill
        if (skill instanceof TessereSkill ts) {
            executeSkill(rpg, ts, msg);
        }
    }

    // ── Execution ─────────────────────────────────────────────────────────────

    private void executeSkill(RPGPlayer rpg, TessereSkill ts, MessageManager msg) {
        Player player = rpg.getBukkitPlayer();
        double score = rpg.getTessereLoadout().getEfficaciteScore();

        // Passive skills cannot be manually activated
        if (ts.getType() == SkillType.PASSIVE) {
            player.sendMessage(msg.getMessageNoPrefix(
                    MessagesEnum.TESSERE.SKILL_MODE.PASSIVE_ONLY.getPath(),
                    "%skill%", ts.getDisplayName()));
            return;
        }

        // Cooldown check using RPGPlayer.cooldowns
        String cdKey = "skill_" + ts.name();
        if (rpg.isOnCooldown(cdKey)) {
            player.sendMessage(msg.getMessageNoPrefix(
                    MessagesEnum.TESSERE.SKILL_MODE.ON_COOLDOWN.getPath(),
                    "%skill%", ts.getDisplayName()));
            return;
        }

        // Execute based on skill type
        switch (ts) {

            // ── ACTIVE skills ─────────────────────────────────────────────────

            case BOND_INSTANTANE -> {
                double power = ts.param("puissance", score);
                player.setVelocity(player.getLocation().getDirection().multiply(power).setY(0.5));
                long cdTicks = (long) ts.param("cooldown_ticks", score);
                rpg.setCooldown(cdKey, cdTicks * 50L); // ticks → ms
            }

            case CHOC_DIMENSIONNEL -> {
                double rayon  = ts.param("rayon", score);
                double degats = ts.param("degats", score);
                player.getWorld().getNearbyEntities(player.getLocation(), rayon, rayon, rayon).forEach(entity -> {
                    if (entity.equals(player)) return;
                    if (entity instanceof LivingEntity){
                        ((LivingEntity) entity).damage(degats, player);
                        entity.setVelocity(entity.getLocation().toVector()
                                .subtract(player.getLocation().toVector())
                                .normalize().setY(0.4).multiply(0.8));
                    }
                });
                long cdTicks = (long) ts.param("cooldown_ticks", score);
                rpg.setCooldown(cdKey, cdTicks * 50L);
            }

            // ── TRIGGERED skills — fire their effect once on activation ───────

            case DECLIN_FORCE -> {
                // Apply weakness to all nearby enemies for 5 seconds
                player.getWorld().getNearbyEntities(player.getLocation(), 6.0, 6.0, 6.0).forEach(entity -> {
                    if (!entity.equals(player) && entity instanceof LivingEntity)
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(
                                PotionEffectType.WEAKNESS, (int) ts.param("duree_ticks", score), 0));
                });
                rpg.setCooldown(cdKey, 10_000L); // 10 s default
            }

            case CHAOS_LATENT -> {
                // Random multiplier burst applied as a raw buff for 3s
                double multMin = ts.paramBrut("mult_min");
                double multMax = ts.paramBrut("mult_max");
                double mult    = multMin + Math.random() * (multMax - multMin);
                if (mult > 1.0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, 1));
                    player.sendMessage(msg.getMessageNoPrefix(
                            MessagesEnum.TESSERE.COMBAT.CHAOS_SURGE.getPath()));
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0));
                    player.sendMessage(msg.getMessageNoPrefix(
                            MessagesEnum.TESSERE.COMBAT.CHAOS_DISSIPATION.getPath()));
                }
                rpg.setCooldown(cdKey, 15_000L);
            }

            default -> {
                // Unhandled skill type — just notify
                player.sendMessage(msg.getMessageNoPrefix(
                        MessagesEnum.TESSERE.SKILL_MODE.PASSIVE_ONLY.getPath(),
                        "%skill%", ts.getDisplayName()));
                return;
            }
        }

        // Activation confirmation message
        player.sendMessage(msg.getMessageNoPrefix(
                MessagesEnum.TESSERE.SKILL_MODE.ACTIVATED.getPath(),
                "%skill%", ts.getDisplayName()));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Builds the ItemStack displayed in the hotbar for a given skill slot.
     * Uses the skill's existing icon, adds an index in PDC, and enriches the lore.
     */
    private ItemStack buildSkillItem(Skill skill, int index, MessageManager msg) {
        ItemStack item = skill.getIcon(); // defined in TessereSkill
        ItemMeta meta  = item.getItemMeta();
        if (meta == null) return item;

        // Enrich lore
        List<String> lore = meta.getLore() != null
                ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("");
        if (skill instanceof TessereSkill ts && ts.getType() == SkillType.PASSIVE) {
            lore.add(ChatColor.DARK_GRAY + "Passive — always active.");
        } else {
            lore.add(ChatColor.YELLOW + "Click to activate.");
        }
        meta.setLore(lore);

        // Store slot index in PDC so activation knows which skill was clicked
        meta.getPersistentDataContainer().set(
                KEY_SKILL_INDEX, PersistentDataType.INTEGER, index);
        item.setItemMeta(meta);
        return item;
    }
}
