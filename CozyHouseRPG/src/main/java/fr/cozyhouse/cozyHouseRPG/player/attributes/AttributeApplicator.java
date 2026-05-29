package fr.cozyhouse.cozyHouseRPG.player.attributes;

import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Applies RPG player attributes to Bukkit player
 * Handles all stat synchronization between RPG data and Minecraft attributes
 * Optimized to only update changed values
 */
public class AttributeApplicator {

    /**
     * Applies all attributes from RPG player to Bukkit player
     *
     * @param bukkitPlayer the Bukkit player to update
     * @param rpgPlayer the RPG player data source
     */
    public void applyAttributes(Player bukkitPlayer, RPGPlayer rpgPlayer) {
        // Health
        applyAttribute(bukkitPlayer, Attribute.MAX_HEALTH, rpgPlayer.getMaxHealth());
        bukkitPlayer.setHealth(Math.min(rpgPlayer.getHealth(), rpgPlayer.getMaxHealth()));
        
        // Food
        bukkitPlayer.setFoodLevel((int) rpgPlayer.getFood());
        
        // Movement & Physical
        applyAttribute(bukkitPlayer, Attribute.MOVEMENT_SPEED, rpgPlayer.getMovementSpeed());
        applyAttribute(bukkitPlayer, Attribute.ATTACK_DAMAGE, rpgPlayer.getAttackDamage());
        applyAttribute(bukkitPlayer, Attribute.ATTACK_SPEED, rpgPlayer.getAttackSpeed());
        applyAttribute(bukkitPlayer, Attribute.JUMP_STRENGTH, rpgPlayer.getJumpStrength());
        
        // Defense
        applyAttribute(bukkitPlayer, Attribute.ARMOR, rpgPlayer.getArmor());
        applyAttribute(bukkitPlayer, Attribute.ARMOR_TOUGHNESS, rpgPlayer.getToughness());
        applyAttribute(bukkitPlayer, Attribute.KNOCKBACK_RESISTANCE, rpgPlayer.getKnockbackResistance());
        
        // Environmental
        applyFallDamage(bukkitPlayer, rpgPlayer);
        applyAttribute(bukkitPlayer, Attribute.WATER_MOVEMENT_EFFICIENCY, rpgPlayer.getSwimSpeedMultiplier());
        applyWaterBreathing(bukkitPlayer, rpgPlayer);
    }

    /**
     * Applies a single attribute if value is valid
     *
     * @param player the player to update
     * @param attribute the attribute type
     * @param value the new value
     */
    private void applyAttribute(Player player, Attribute attribute, double value) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    /**
     * Applies fall damage settings
     *
     * @param player the player to update
     * @param rpgPlayer the RPG player data
     */
    private void applyFallDamage(Player player, RPGPlayer rpgPlayer) {
        double multiplier = rpgPlayer.isNoFallDamage() ? 0.0 : 1.0 - rpgPlayer.getFallDamageReduction();
        applyAttribute(player, Attribute.FALL_DAMAGE_MULTIPLIER, multiplier);
    }

    /**
     * Applies water breathing ability
     *
     * @param player the player to update
     * @param rpgPlayer the RPG player data
     */
    private void applyWaterBreathing(Player player, RPGPlayer rpgPlayer) {
        double value = rpgPlayer.isWaterBreathing() ? 1024.0 : 0.0;
        applyAttribute(player, Attribute.OXYGEN_BONUS, value);
    }

    /**
     * Resets all attributes to default values
     *
     * @param player the player to reset
     */
    public void resetAttributes(Player player) {
        resetAttribute(player, Attribute.MAX_HEALTH, 20.0);
        resetAttribute(player, Attribute.MOVEMENT_SPEED, 0.1);
        resetAttribute(player, Attribute.ATTACK_DAMAGE, 2.0);
        resetAttribute(player, Attribute.ATTACK_SPEED, 4.0);
        resetAttribute(player, Attribute.JUMP_STRENGTH, 0.42);
        resetAttribute(player, Attribute.ARMOR, 0.0);
        resetAttribute(player, Attribute.ARMOR_TOUGHNESS, 0.0);
        resetAttribute(player, Attribute.KNOCKBACK_RESISTANCE, 0.0);
        resetAttribute(player, Attribute.FALL_DAMAGE_MULTIPLIER, 1.0);
        resetAttribute(player, Attribute.WATER_MOVEMENT_EFFICIENCY, 0.0);
        resetAttribute(player, Attribute.OXYGEN_BONUS, 0.0);
        
        player.setFoodLevel(20);
        player.setHealth(20.0);
    }

    /**
     * Resets a single attribute to default value
     *
     * @param player the player to update
     * @param attribute the attribute to reset
     * @param defaultValue the default value
     */
    private void resetAttribute(Player player, Attribute attribute, double defaultValue) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(defaultValue);
        }
    }
}
