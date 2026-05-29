package fr.cozyhouse.cozyHouseRPG.player;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.experience.ExperienceService;
import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import fr.cozyhouse.cozyHouseRPG.skills.playerstate.TessereLoadout;
import fr.cozyhouse.cozyHouseRPG.status.Status;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Represents an RPG player's data
 * Pure data class - no business logic
 * Use PlayerService for operations
 */
@Data
public class RPGPlayer {

    private final PluginContext context;

    private final UUID playerId;
    private transient Player bukkitPlayer;

    // Race & Level
    private RaceType currentRace;
    private double level;
    private long experience;

    // Core Stats
    private double health;
    private double maxHealth;
    private double mana;
    private double maxMana;
    private double food;

    // Combat Attributes
    private double strength;
    private double dexterity;
    private double constitution;
    private double intelligence;
    private double charisma;
    private float critChance;
    private float critDamage;
    private float dodgeChance;

    // Physical Stats
    private double movementSpeed;
    private double attackDamage;
    private float attackSpeed;
    private float jumpStrength;
    private float knockbackResistance;
    private float armor;
    private float toughness;

    // Resistances
    private double fallDamageReduction;
    private boolean noFallDamage;
    private double swimSpeedMultiplier;
    private double miningFatigueResistance;
    private double poisonResistance;

    // Special Abilities
    private final List<PotionEffect> activeEffects = new ArrayList<>();
    private boolean sunlightWeakness;
    private boolean waterBreathing;
    private boolean canClimbWalls;

    // Economic
    private double expBonus;
    private double lootBonus;
    private double tradeDiscount;

    // Status & Cooldowns
    private final List<Status> activeStatuses = new ArrayList<>();
    private final Map<String, Long> cooldowns = new HashMap<>();

    private final ExperienceService experienceService;

    // Skills
    private int skillPoints;
    private int skillPointsPerLevel;

    // Tessere
    private final TessereLoadout tessereLoadout = new TessereLoadout();

    // Skill mode
    /** True when the player is in skill selection mode. */
    private boolean inSkillMode = false;
    /**
     * Snapshot of the player's full inventory (36 slots) taken when entering skill mode.
     * Restored automatically when skill mode ends.
     */
    private ItemStack[] savedInventory = null;

    public RPGPlayer(PluginContext context, UUID playerId) {
        this.playerId = playerId;
        this.context = context;
        this.skillPointsPerLevel = 3;
        this.experienceService = new ExperienceService(context, this);
    }


    /**
     * Checks if player has chosen a race
     */
    public boolean hasRace() {
        return currentRace != null;
    }

    /**
     * Checks if an ability is on cooldown
     */
    public boolean isOnCooldown(String abilityName) {
        Long endTime = cooldowns.get(abilityName);
        if (endTime == null) return false;
        
        if (System.currentTimeMillis() >= endTime) {
            cooldowns.remove(abilityName);
            return false;
        }
        return true;
    }

    /**
     * Sets a cooldown for an ability
     */
    public void setCooldown(String abilityName, long durationMillis) {
        cooldowns.put(abilityName, System.currentTimeMillis() + durationMillis);
    }

    /**
     * Gets remaining cooldown time in milliseconds
     */
    public long getRemainingCooldown(String abilityName) {
        Long endTime = cooldowns.get(abilityName);
        if (endTime == null) return 0;
        
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * Add skill points
     */
    public void addSkillPoint(int number){
        this.skillPoints = this.skillPoints + number;
    }

    /**
     * Level up player
     */
    public void levelUp(){
        setLevel(this.level + 1);
        addSkillPoint(this.skillPointsPerLevel);
    }
}
