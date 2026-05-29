package fr.cozyhouse.cozyHouseRPG.player.experience;

import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import lombok.Getter;
import org.bukkit.attribute.Attribute;

import java.util.Objects;


public class ExperienceService {

    private final PluginContext context;
    private final RPGPlayer rpgPlayer;
    /**
     * -- GETTER --
     *
     * @return le niveau maximum du système.
     */
    @Getter
    private final double maxLevel;

    public ExperienceService(PluginContext context, RPGPlayer rpgPlayer) {
        this.context = context;
        this.rpgPlayer = rpgPlayer;
        this.maxLevel = 20;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helpers d'affichage niveau / XP
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * @return true si le joueur a atteint le niveau maximum.
     */
    public boolean isMaxLevel() {
        return rpgPlayer.getLevel() >= maxLevel;
    }

    /**
     * Retourne le niveau formaté pour l'affichage :
     *   - Niveau max → "§6✦ MAX"
     *   - Sinon      → le numéro (ex : "12")
     */
    public String getLevelDisplay() {
        return isMaxLevel()
                ? context.getMessageManager().getMessageNoPrefix(MessagesEnum.RPG.EXPERIENCE.LEVEL_MAX_DISPLAY.getPath())
                : String.valueOf((int) rpgPlayer.getLevel());
    }

    /**
     * Retourne l'XP formatée sous la forme "actuel / requis" :
     *   - Niveau max → "§6MAX"
     *   - Sinon      → ex : "1450 / 4100"
     */
    public String getXpDisplay() {
        if (isMaxLevel()) return context.getMessageManager()
                .getMessageNoPrefix(MessagesEnum.RPG.EXPERIENCE.XP_MAX_DISPLAY.getPath());
        long current  = rpgPlayer.getExperience();
        long required = calculateRequiredExperience(rpgPlayer.getLevel());
        return current + " / " + required;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Gestion de l'expérience
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Définit l'XP du joueur directement puis vérifie si un level-up est dû.
     * À utiliser pour les commandes admin (/chrpg exp set).
     * Si le joueur est déjà au niveau max, l'XP est bloquée à 0.
     *
     * @param amount the exact XP amount to set (clamped to >= 0)
     */
    public void setExperienceAndCheck(long amount) {
        if (isMaxLevel()) {
            rpgPlayer.setExperience(0);
            return;
        }
        rpgPlayer.setExperience(Math.max(0, amount));
        checkLevelUp();
    }

    /**
     * Adds experience to a player.
     *
     * @param amount base experience amount
     * @return final amount added after bonus multiplier (0 if max level already reached)
     */
    public long addExperience(long amount) {
        if (isMaxLevel()) return 0L;

        double multiplier = 1.0 + rpgPlayer.getExpBonus();
        long finalAmount = (long) (amount * multiplier);

        rpgPlayer.setExperience(rpgPlayer.getExperience() + finalAmount);

        checkLevelUp();

        return finalAmount;
    }

    /**
     * Checks if the player should level up and processes it.
     */
    private void checkLevelUp() {
        long requiredExp = calculateRequiredExperience(rpgPlayer.getLevel());

        while (rpgPlayer.getExperience() >= requiredExp) {
            rpgPlayer.levelUp();
            rpgPlayer.setExperience(rpgPlayer.getExperience() - requiredExp);
            applyLevelupBonus();

            if (isMaxLevel()) {
                // Niveau max atteint : on bloque l'XP à 0 et on notifie le joueur
                rpgPlayer.setExperience(0);
                context.getMessageManager().sendNoPrefix(rpgPlayer.getBukkitPlayer(),
                        MessagesEnum.RPG.EXPERIENCE.MAX_LEVEL_REACHED.getPath());
                break;
            }

            context.getMessageManager().sendNoPrefix(rpgPlayer.getBukkitPlayer(),
                    MessagesEnum.RPG.EXPERIENCE.LEVEL_UP.getPath(),
                    "%level%", String.valueOf((int) rpgPlayer.getLevel()),
                    "%required%", String.valueOf(calculateRequiredExperience(rpgPlayer.getLevel())));

            requiredExp = calculateRequiredExperience(rpgPlayer.getLevel());
        }
    }

    /**
     * Calculates experience required to reach the next level.
     *
     * @param currentLevel the current level
     * @return required experience
     */
    public long calculateRequiredExperience(double currentLevel) {
        // Formule : 100 * (level + 1)²
        return (long) (100 * Math.pow(currentLevel + 1, 2));
    }

    /**
     * Applies bonus effects on level up (health refill).
     */
    private void applyLevelupBonus() {
        rpgPlayer.getBukkitPlayer().setHealth(
                Objects.requireNonNull(
                        rpgPlayer.getBukkitPlayer().getAttribute(Attribute.MAX_HEALTH)
                ).getValue()
        );
    }
}
