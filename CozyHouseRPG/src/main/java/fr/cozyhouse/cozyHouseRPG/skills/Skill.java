package fr.cozyhouse.cozyHouseRPG.skills;

import fr.cozyhouse.cozyHouseRPG.status.Status;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Interface defining all properties of a player skill
 * Skills can be passive, active, or triggered abilities
 */
public interface Skill {

    int getId();
    String getName();
    String getDisplayName();
    String getDescription();

    SkillType getType();
    SkillCategory getCategory();
    boolean isUnlocked();
    boolean isEnabled();

    int getMaxLevel();
    int getCurrentLevel();

    ChatColor getColor();
    ItemStack getIcon();

    long getCooldownTicks();
    long getRemainingCooldownTicks();

    /**
     * Gets the base value of this skill
     *
     * @return base value
     */
    double getValue();

    /**
     * Gets the value increase per level
     *
     * @return value per level
     */
    double getValuePerLevel();

    /**
     * Calculates the bonus at a specific level
     *
     * @param level the level to calculate for
     * @return total bonus at that level
     */
    double getBonusAtLevel(int level);

    List<Status> getGrantedStatuses();
    List<String> getUnlockedAtLevels();

    boolean isPassive();
    boolean hasLevels();
    boolean isToggleable();
    boolean consumesItem();
    Material getConsumableItem();
}
