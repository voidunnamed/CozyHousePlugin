package fr.cozyhouse.cozyHouseRPG.race;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating race instances with predefined stats
 * Centralizes race configuration and ensures consistency
 */
public final class RaceFactory {

    private final PluginContext context;
    private final MessageManager messageManager;

    public RaceFactory(PluginContext context) {
        this.context = context;
        this.messageManager = context.getMessageManager();
    }

    /**
     * Creates a race instance based on type
     *
     * @param type the race type to create
     * @return configured race instance
     */
    public Race createRace(RaceType type) {
        return switch (type) {
            case HUMAN -> createHuman();
            case ELF -> createElf();
            case DWARF -> createDwarf();
            case ORC -> createOrc();
            case GOBLIN -> createGoblin();
            case FAIRY -> createFairy();
            case BEAST -> createBeast();
            case FISH -> createFish();
        };
    }

    /**
     * Creates the Human race - balanced and versatile
     */
    private Race createHuman() {
        Map<Biome, Double> biomeSpeed = new HashMap<>();
        biomeSpeed.put(Biome.PLAINS, 1.05);
        biomeSpeed.put(Biome.SUNFLOWER_PLAINS, 1.05);

        return Race.builder()
                .type(RaceType.HUMAN)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.HUMAN.DISPLAY_NAME.getPath()))
                .prefix("§f[Humain]")
                .chatColor(ChatColor.WHITE)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.HUMAN.DESCRIPTION.getPath()))
                .icon(createIcon(Material.PLAYER_HEAD, "§fHumain"))
                
                .baseHealth(20.0)
                .baseStrength(10.0)
                .baseDexterity(10.0)
                .baseConstitution(10.0)
                .baseIntelligence(10.0)
                .baseCharisma(12.0)
                
                .baseCritChance(5.0f)
                .baseCritDamage(1.5f)
                .baseDodgeChance(5.0f)
                
                .baseMovementSpeed(0.1f)
                .baseAttackDamage(2.0f)
                .baseAttackSpeed(4.0f)
                .baseJumpStrength(0.42f)
                .baseKnockbackResistance(0.0f)
                .baseArmor(0.0f)
                .baseToughness(0.0f)
                
                .fallDamageReduction(0.0)
                .noFallDamage(false)
                .swimSpeedMultiplier(1.0)
                .miningFatigueResistance(0.0)
                .poisonResistance(0.0)
                
                .sunlightWeakness(false)
                .waterBreathing(false)
                .canClimbWalls(false)
                
                .expBonus(0.10)
                .lootBonus(0.05)
                .tradeDiscount(0.10)
                
                .biomeSpeedMultipliers(biomeSpeed)
                .build();
    }

    /**
     * Creates the Elf race - agile forest dweller
     */
    private Race createElf() {
        Map<Biome, Double> biomeSpeed = new HashMap<>();
        biomeSpeed.put(Biome.FOREST, 1.15);
        biomeSpeed.put(Biome.BIRCH_FOREST, 1.15);
        biomeSpeed.put(Biome.DARK_FOREST, 1.10);

        return Race.builder()
                .type(RaceType.ELF)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.ELF.DISPLAY_NAME.getPath()))
                .prefix("§a[Elfe]")
                .chatColor(ChatColor.GREEN)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.ELF.DESCRIPTION.getPath()))
                .icon(createIcon(Material.BOW, "§aElfe"))
                
                .baseHealth(18.0)
                .baseStrength(8.0)
                .baseDexterity(14.0)
                .baseConstitution(8.0)
                .baseIntelligence(12.0)
                .baseCharisma(11.0)
                
                .baseCritChance(10.0f)
                .baseCritDamage(1.75f)
                .baseDodgeChance(12.0f)
                
                .baseMovementSpeed(0.11f)
                .baseAttackDamage(1.8f)
                .baseAttackSpeed(4.5f)
                .baseJumpStrength(0.45f)
                .baseKnockbackResistance(0.0f)
                .baseArmor(0.0f)
                .baseToughness(0.0f)
                
                .fallDamageReduction(0.25)
                .noFallDamage(false)
                .swimSpeedMultiplier(0.9)
                .miningFatigueResistance(0.0)
                .poisonResistance(0.3)
                
                .sunlightWeakness(false)
                .waterBreathing(false)
                .canClimbWalls(false)
                
                .expBonus(0.15)
                .lootBonus(0.10)
                .tradeDiscount(0.05)
                
                .biomeSpeedMultipliers(biomeSpeed)
                .build();
    }

    /**
     * Creates the Dwarf race - sturdy mountain dweller
     */
    private Race createDwarf() {
        Map<Biome, Double> biomeSpeed = new HashMap<>();
        biomeSpeed.put(Biome.JAGGED_PEAKS, 1.10);
        biomeSpeed.put(Biome.STONY_PEAKS, 1.10);

        return Race.builder()
                .type(RaceType.DWARF)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.DWARF.DISPLAY_NAME.getPath()))
                .prefix("§6[Nain]")
                .chatColor(ChatColor.GOLD)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.DWARF.DESCRIPTION.getPath()))
                .icon(createIcon(Material.IRON_PICKAXE, "§6Nain"))
                
                .baseHealth(24.0)
                .baseStrength(13.0)
                .baseDexterity(7.0)
                .baseConstitution(14.0)
                .baseIntelligence(9.0)
                .baseCharisma(8.0)
                
                .baseCritChance(3.0f)
                .baseCritDamage(1.4f)
                .baseDodgeChance(2.0f)
                
                .baseMovementSpeed(0.09f)
                .baseAttackDamage(2.5f)
                .baseAttackSpeed(3.8f)
                .baseJumpStrength(0.38f)
                .baseKnockbackResistance(0.3f)
                .baseArmor(2.0f)
                .baseToughness(1.0f)
                
                .fallDamageReduction(0.0)
                .noFallDamage(false)
                .swimSpeedMultiplier(0.7)
                .miningFatigueResistance(0.5)
                .poisonResistance(0.4)
                
                .sunlightWeakness(false)
                .waterBreathing(false)
                .canClimbWalls(false)
                
                .expBonus(0.05)
                .lootBonus(0.20)
                .tradeDiscount(0.15)
                
                .biomeSpeedMultipliers(biomeSpeed)
                .build();
    }

    /**
     * Creates the Orc race - brutal warrior
     */
    private Race createOrc() {
        return Race.builder()
                .type(RaceType.ORC)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.ORC.DISPLAY_NAME.getPath()))
                .prefix("§c[Orc]")
                .chatColor(ChatColor.RED)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.ORC.DESCRIPTION.getPath()))
                .icon(createIcon(Material.IRON_AXE, messageManager.getMessageNoPrefix(MessagesEnum.RACE.ORC.DISPLAY_NAME.getPath())))
                
                .baseHealth(26.0)
                .baseStrength(15.0)
                .baseDexterity(6.0)
                .baseConstitution(13.0)
                .baseIntelligence(6.0)
                .baseCharisma(5.0)
                
                .baseCritChance(8.0f)
                .baseCritDamage(2.0f)
                .baseDodgeChance(1.0f)
                
                .baseMovementSpeed(0.095f)
                .baseAttackDamage(3.0f)
                .baseAttackSpeed(3.5f)
                .baseJumpStrength(0.40f)
                .baseKnockbackResistance(0.2f)
                .baseArmor(1.0f)
                .baseToughness(0.5f)
                
                .fallDamageReduction(0.0)
                .noFallDamage(false)
                .swimSpeedMultiplier(0.8)
                .miningFatigueResistance(0.2)
                .poisonResistance(0.2)
                
                .sunlightWeakness(false)
                .waterBreathing(false)
                .canClimbWalls(false)
                
                .expBonus(0.00)
                .lootBonus(0.15)
                .tradeDiscount(-0.05)
                
                .build();
    }

    /**
     * Creates the Goblin race - cunning and fast
     */
    private Race createGoblin() {
        return Race.builder()
                .type(RaceType.GOBLIN)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.GOBLIN.DISPLAY_NAME.getPath()))
                .prefix("§e[Gobelin]")
                .chatColor(ChatColor.YELLOW)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.GOBLIN.DESCRIPTION.getPath()))
                .icon(createIcon(Material.GOLD_NUGGET, messageManager.getMessageNoPrefix(MessagesEnum.RACE.GOBLIN.DISPLAY_NAME.getPath())))
                
                .baseHealth(16.0)
                .baseStrength(7.0)
                .baseDexterity(13.0)
                .baseConstitution(7.0)
                .baseIntelligence(11.0)
                .baseCharisma(6.0)
                
                .baseCritChance(12.0f)
                .baseCritDamage(1.6f)
                .baseDodgeChance(15.0f)
                
                .baseMovementSpeed(0.12f)
                .baseAttackDamage(1.5f)
                .baseAttackSpeed(5.0f)
                .baseJumpStrength(0.44f)
                .baseKnockbackResistance(0.0f)
                .baseArmor(0.0f)
                .baseToughness(0.0f)
                
                .fallDamageReduction(0.15)
                .noFallDamage(false)
                .swimSpeedMultiplier(0.9)
                .miningFatigueResistance(0.0)
                .poisonResistance(0.1)
                
                .sunlightWeakness(true)
                .waterBreathing(false)
                .canClimbWalls(false)
                
                .expBonus(0.08)
                .lootBonus(0.25)
                .tradeDiscount(0.20)
                
                .build();
    }

    /**
     * Creates the Fairy race - magical flying creature
     */
    private Race createFairy() {
        return Race.builder()
                .type(RaceType.FAIRY)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.FAIRY.DISPLAY_NAME.getPath()))
                .prefix("§d[Fée]")
                .chatColor(ChatColor.LIGHT_PURPLE)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.FAIRY.DESCRIPTION.getPath()))
                .icon(createIcon(Material.FEATHER, messageManager.getMessageNoPrefix(MessagesEnum.RACE.FAIRY.DISPLAY_NAME.getPath())))
                
                .baseHealth(14.0)
                .baseStrength(5.0)
                .baseDexterity(11.0)
                .baseConstitution(6.0)
                .baseIntelligence(15.0)
                .baseCharisma(14.0)
                
                .baseCritChance(7.0f)
                .baseCritDamage(1.8f)
                .baseDodgeChance(10.0f)
                
                .baseMovementSpeed(0.11f)
                .baseAttackDamage(1.2f)
                .baseAttackSpeed(4.2f)
                .baseJumpStrength(0.50f)
                .baseKnockbackResistance(0.0f)
                .baseArmor(0.0f)
                .baseToughness(0.0f)
                
                .fallDamageReduction(0.5)
                .noFallDamage(true)
                .swimSpeedMultiplier(1.0)
                .miningFatigueResistance(0.0)
                .poisonResistance(0.2)
                
                .sunlightWeakness(false)
                .waterBreathing(false)
                .canClimbWalls(false)
                
                .expBonus(0.20)
                .lootBonus(0.05)
                .tradeDiscount(0.10)
                
                .build();
    }

    /**
     * Creates the Beast race - savage and powerful
     */
    private Race createBeast() {
        return Race.builder()
                .type(RaceType.BEAST)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.BEAST.DISPLAY_NAME.getPath()))
                .prefix("§8[Bête]")
                .chatColor(ChatColor.DARK_GRAY)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.BEAST.DESCRIPTION.getPath()))
                .icon(createIcon(Material.LEATHER, messageManager.getMessageNoPrefix(MessagesEnum.RACE.BEAST.DISPLAY_NAME.getPath())))
                
                .baseHealth(22.0)
                .baseStrength(14.0)
                .baseDexterity(12.0)
                .baseConstitution(11.0)
                .baseIntelligence(5.0)
                .baseCharisma(4.0)
                
                .baseCritChance(15.0f)
                .baseCritDamage(2.2f)
                .baseDodgeChance(8.0f)
                
                .baseMovementSpeed(0.115f)
                .baseAttackDamage(2.8f)
                .baseAttackSpeed(4.3f)
                .baseJumpStrength(0.46f)
                .baseKnockbackResistance(0.1f)
                .baseArmor(0.5f)
                .baseToughness(0.0f)
                
                .fallDamageReduction(0.2)
                .noFallDamage(false)
                .swimSpeedMultiplier(1.1)
                .miningFatigueResistance(0.0)
                .poisonResistance(0.15)
                
                .sunlightWeakness(false)
                .waterBreathing(false)
                .canClimbWalls(true)
                
                .expBonus(0.00)
                .lootBonus(0.10)
                .tradeDiscount(-0.10)
                
                .build();
    }

    /**
     * Creates the Fish race - aquatic being
     */
    private Race createFish() {
        Map<Biome, Double> biomeSpeed = new HashMap<>();
        biomeSpeed.put(Biome.OCEAN, 1.3);
        biomeSpeed.put(Biome.DEEP_OCEAN, 1.3);
        biomeSpeed.put(Biome.RIVER, 1.2);

        return Race.builder()
                .type(RaceType.FISH)
                .displayName(messageManager.getMessageNoPrefix(MessagesEnum.RACE.FISH.DISPLAY_NAME.getPath()))
                .prefix("§b[Poisson]")
                .chatColor(ChatColor.AQUA)
                .description(messageManager.getMessageNoPrefix(MessagesEnum.RACE.FISH.DESCRIPTION.getPath()))
                .icon(createIcon(Material.COD, messageManager.getMessageNoPrefix(MessagesEnum.RACE.FISH.DISPLAY_NAME.getPath())))
                
                .baseHealth(18.0)
                .baseStrength(9.0)
                .baseDexterity(11.0)
                .baseConstitution(10.0)
                .baseIntelligence(8.0)
                .baseCharisma(7.0)
                
                .baseCritChance(6.0f)
                .baseCritDamage(1.5f)
                .baseDodgeChance(9.0f)
                
                .baseMovementSpeed(0.085f)
                .baseAttackDamage(1.9f)
                .baseAttackSpeed(4.0f)
                .baseJumpStrength(0.40f)
                .baseKnockbackResistance(0.0f)
                .baseArmor(0.0f)
                .baseToughness(0.0f)
                
                .fallDamageReduction(0.0)
                .noFallDamage(false)
                .swimSpeedMultiplier(1.5)
                .miningFatigueResistance(0.0)
                .poisonResistance(0.5)
                
                .sunlightWeakness(false)
                .waterBreathing(true)
                .canClimbWalls(false)
                
                .expBonus(0.05)
                .lootBonus(0.15)
                .tradeDiscount(0.05)
                
                .biomeSpeedMultipliers(biomeSpeed)
                .build();
    }

    /**
     * Creates an icon ItemStack with display name
     */
    private ItemStack createIcon(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }
}
