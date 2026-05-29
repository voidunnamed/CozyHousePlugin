package fr.cozyhouse.cozyHouseRPG.race;

import fr.cozyhouse.cozyHouseRPG.status.Status;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Immutable race data class
 * Use RaceBuilder to create instances
 * All modifications return new instances (immutable pattern)
 */
@Getter
@Builder(toBuilder = true)
public final class Race {

    // Identity
    private final RaceType type;
    private final String displayName;
    private final String prefix;
    private final ChatColor chatColor;
    private final String description;
    private final ItemStack icon;

    // Base Stats
    private final double baseHealth;
    private final double baseStrength;
    private final double baseDexterity;
    private final double baseConstitution;
    private final double baseIntelligence;
    private final double baseCharisma;

    // Combat Stats
    private final float baseCritChance;
    private final float baseCritDamage;
    private final float baseDodgeChance;

    // Movement & Physical
    private final float baseMovementSpeed;
    private final float baseAttackDamage;
    private final float baseAttackSpeed;
    private final float baseJumpStrength;
    private final float baseKnockbackResistance;
    private final float baseArmor;
    private final float baseToughness;

    // Resistances
    private final double fallDamageReduction;
    private final boolean noFallDamage;
    private final double swimSpeedMultiplier;
    private final double miningFatigueResistance;
    private final double poisonResistance;

    // Special Abilities
    @Builder.Default
    private final List<PotionEffect> permanentEffects = new ArrayList<>();
    private final boolean sunlightWeakness;
    private final boolean waterBreathing;
    private final boolean canClimbWalls;

    // Economic Bonuses
    private final double expBonus;
    private final double lootBonus;
    private final double tradeDiscount;

    // Biome Interactions (immutable maps)
    @Builder.Default
    private final Map<Biome, Double> biomeSpeedMultipliers = new HashMap<>();
    @Builder.Default
    private final Map<Biome, Double> biomeDamageMultipliers = new HashMap<>();
    @Builder.Default
    private final Map<Biome, Status> biomeStatuses = new HashMap<>();

    /**
     * Gets speed multiplier for a biome
     *
     * @param biome the biome to check
     * @return speed multiplier (1.0 = normal speed)
     */
    public double getBiomeSpeedMultiplier(Biome biome) {
        return biomeSpeedMultipliers.getOrDefault(biome, 1.0);
    }

    /**
     * Gets damage multiplier for a biome
     *
     * @param biome the biome to check
     * @return damage multiplier (1.0 = normal damage)
     */
    public double getBiomeDamageMultiplier(Biome biome) {
        return biomeDamageMultipliers.getOrDefault(biome, 1.0);
    }

    /**
     * Gets status effect for a biome
     *
     * @param biome the biome to check
     * @return status effect or null
     */
    public Status getBiomeStatus(Biome biome) {
        return biomeStatuses.get(biome);
    }

    /**
     * Returns unmodifiable view of permanent effects
     */
    public List<PotionEffect> getPermanentEffects() {
        return Collections.unmodifiableList(permanentEffects);
    }

    /**
     * Creates a deep copy of this icon
     */
    public ItemStack getIconCopy() {
        return icon != null ? icon.clone() : new ItemStack(Material.BARRIER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Race race)) return false;
        return type == race.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "Race{type=" + type + ", displayName='" + displayName + "'}";
    }
}
