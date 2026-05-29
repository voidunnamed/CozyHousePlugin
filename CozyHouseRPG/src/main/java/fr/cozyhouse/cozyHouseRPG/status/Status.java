package fr.cozyhouse.cozyHouseRPG.status;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.Set;

/**
 * Interface defining a status effect that can be applied to players
 * Status effects modify player stats and can have visual/audio feedback
 */
public interface Status {

    Effect getEffect();
    int getID();
    String getDisplayName();
    String getDescription();

    Long getTimeEffect();

    Integer getAmplifier();
    Integer getStacks();
    Integer getMaxStacks();

    ChatColor getColor();
    Particle getParticle();
    Sound getApplySound();
    Sound getTickSound();
    int getTickIntervalTicks();

    /**
     * Damage dealt per tick
     *
     * @return damage amount
     */
    double getDamagePerTick();

    /**
     * Multiplier for damage dealt by affected player
     *
     * @return damage multiplier (1.0 = normal)
     */
    double getDamageMultiplier();

    /**
     * Multiplier for defense of affected player
     *
     * @return defense multiplier (1.0 = normal)
     */
    double getDefenseMultiplier();

    /**
     * Multiplier for movement speed
     *
     * @return speed multiplier (1.0 = normal)
     */
    double getSpeedMultiplier();

    double getCritChanceBonus();
    double getCritDamageBonus();
    double getDodgeChanceBonus();
    double getManaRegenBonus();
    double getHealthRegenBonus();
    double getManaCostReduction();

    Set<StatusFlagEnum> getFlags();
}
