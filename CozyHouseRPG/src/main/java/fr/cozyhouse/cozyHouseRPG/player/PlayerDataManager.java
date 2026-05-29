package fr.cozyhouse.cozyHouseRPG.player;

import fr.cozyhouse.cozyHouseRPG.CozyHouseRPG;
import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSkill;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import fr.cozyhouse.cozyHouseRPG.skills.playerstate.TessereLoadout;
import fr.cozyhouse.cozyHouseRPG.status.Status;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Gère la persistance des données d'un RPGPlayer.
 *
 */
public class PlayerDataManager {

    // Namespace utilisé pour toutes les NamespacedKeys du plugin
    private static final String NAMESPACE = "cozyhouserpg";

    private final CozyHouseRPG plugin;
    private final Logger log;

    /** Dossier plugins/CozyHouseRPG/players/ */
    private final File playersFolder;

    // ── NamespacedKeys ────────────────────────────────────────────────────────

    // Race & progression
    private final NamespacedKey KEY_RACE;
    private final NamespacedKey KEY_LEVEL;
    private final NamespacedKey KEY_EXPERIENCE;

    // Vitaux
    private final NamespacedKey KEY_HEALTH;
    private final NamespacedKey KEY_MAX_HEALTH;
    private final NamespacedKey KEY_MANA;
    private final NamespacedKey KEY_MAX_MANA;
    private final NamespacedKey KEY_FOOD;

    // Attributs principaux
    private final NamespacedKey KEY_STRENGTH;
    private final NamespacedKey KEY_DEXTERITY;
    private final NamespacedKey KEY_CONSTITUTION;
    private final NamespacedKey KEY_INTELLIGENCE;
    private final NamespacedKey KEY_CHARISMA;

    // Combat
    private final NamespacedKey KEY_CRIT_CHANCE;
    private final NamespacedKey KEY_CRIT_DAMAGE;
    private final NamespacedKey KEY_DODGE_CHANCE;

    // Stats physiques
    private final NamespacedKey KEY_MOVEMENT_SPEED;
    private final NamespacedKey KEY_ATTACK_DAMAGE;
    private final NamespacedKey KEY_ATTACK_SPEED;
    private final NamespacedKey KEY_JUMP_STRENGTH;
    private final NamespacedKey KEY_KNOCKBACK_RESISTANCE;
    private final NamespacedKey KEY_ARMOR;
    private final NamespacedKey KEY_TOUGHNESS;

    // Résistances & capacités spéciales
    private final NamespacedKey KEY_FALL_DAMAGE_REDUCTION;
    private final NamespacedKey KEY_NO_FALL_DAMAGE;
    private final NamespacedKey KEY_SWIM_SPEED;
    private final NamespacedKey KEY_MINING_FATIGUE_RESISTANCE;
    private final NamespacedKey KEY_POISON_RESISTANCE;
    private final NamespacedKey KEY_SUNLIGHT_WEAKNESS;
    private final NamespacedKey KEY_WATER_BREATHING;
    private final NamespacedKey KEY_CAN_CLIMB_WALLS;

    // Économique
    private final NamespacedKey KEY_EXP_BONUS;
    private final NamespacedKey KEY_LOOT_BONUS;
    private final NamespacedKey KEY_TRADE_DISCOUNT;

    // Skills
    private final NamespacedKey KEY_SKILL_POINTS;
    private final NamespacedKey KEY_SKILL_POINTS_PER_LEVEL;

    // 6 clés pour les slots
    private final NamespacedKey KEY_TESS_HEAD;
    private final NamespacedKey KEY_TESS_CHEST;
    private final NamespacedKey KEY_TESS_RIGHT_ARM;
    private final NamespacedKey KEY_TESS_LEFT_ARM;
    private final NamespacedKey KEY_TESS_RIGHT_LEG;
    private final NamespacedKey KEY_TESS_LEFT_LEG;
    // 3 clés pour l'état révélé
    private final NamespacedKey KEY_TESS_SKILLS;      // CSV : "MARQUE_ARDENTE,PEAU_DE_PIERRE,FLUX_I"
    private final NamespacedKey KEY_TESS_SCORE;       // int = score × 1000
    private final NamespacedKey KEY_TESS_REVEALED;    // byte 1/0
    // Tessères découvertes
    private final NamespacedKey KEY_TESS_DISCOVERED;  // CSV : "THERMIQUE,CINETIQUE,..."

    // ─────────────────────────────────────────────────────────────────────────
    // Constructeur
    // ─────────────────────────────────────────────────────────────────────────

    public PlayerDataManager(CozyHouseRPG plugin) {
        this.plugin = plugin;
        this.log    = plugin.getLogger();
        this.playersFolder = new File(plugin.getDataFolder(), "players");

        if (!playersFolder.exists() && !playersFolder.mkdirs()) {
            log.severe("Impossible de créer le dossier players/ pour CozyHouseRPG.");
        }

        // Race & progression
        KEY_RACE        = key("race");
        KEY_LEVEL       = key("level");
        KEY_EXPERIENCE  = key("experience");

        // Vitaux
        KEY_HEALTH      = key("health");
        KEY_MAX_HEALTH  = key("max_health");
        KEY_MANA        = key("mana");
        KEY_MAX_MANA    = key("max_mana");
        KEY_FOOD        = key("food");

        // Attributs
        KEY_STRENGTH        = key("strength");
        KEY_DEXTERITY       = key("dexterity");
        KEY_CONSTITUTION    = key("constitution");
        KEY_INTELLIGENCE    = key("intelligence");
        KEY_CHARISMA        = key("charisma");

        // Combat
        KEY_CRIT_CHANCE     = key("crit_chance");
        KEY_CRIT_DAMAGE     = key("crit_damage");
        KEY_DODGE_CHANCE    = key("dodge_chance");

        // Physique
        KEY_MOVEMENT_SPEED         = key("movement_speed");
        KEY_ATTACK_DAMAGE          = key("attack_damage");
        KEY_ATTACK_SPEED           = key("attack_speed");
        KEY_JUMP_STRENGTH          = key("jump_strength");
        KEY_KNOCKBACK_RESISTANCE   = key("knockback_resistance");
        KEY_ARMOR                  = key("armor");
        KEY_TOUGHNESS              = key("toughness");

        // Résistances
        KEY_FALL_DAMAGE_REDUCTION     = key("fall_damage_reduction");
        KEY_NO_FALL_DAMAGE            = key("no_fall_damage");
        KEY_SWIM_SPEED                = key("swim_speed");
        KEY_MINING_FATIGUE_RESISTANCE = key("mining_fatigue_resistance");
        KEY_POISON_RESISTANCE         = key("poison_resistance");
        KEY_SUNLIGHT_WEAKNESS         = key("sunlight_weakness");
        KEY_WATER_BREATHING           = key("water_breathing");
        KEY_CAN_CLIMB_WALLS           = key("can_climb_walls");

        // Économique
        KEY_EXP_BONUS       = key("exp_bonus");
        KEY_LOOT_BONUS      = key("loot_bonus");
        KEY_TRADE_DISCOUNT  = key("trade_discount");

        // Skills
        KEY_SKILL_POINTS           = key("skill_points");
        KEY_SKILL_POINTS_PER_LEVEL = key("skill_points_per_level");

        KEY_TESS_HEAD      = key("tess_head");
        KEY_TESS_CHEST     = key("tess_chest");
        KEY_TESS_RIGHT_ARM = key("tess_right_arm");
        KEY_TESS_LEFT_ARM  = key("tess_left_arm");
        KEY_TESS_RIGHT_LEG = key("tess_right_leg");
        KEY_TESS_LEFT_LEG  = key("tess_left_leg");

        KEY_TESS_SKILLS      = key("tess_skills");
        KEY_TESS_SCORE       = key("tess_score");
        KEY_TESS_REVEALED    = key("tess_revealed");
        KEY_TESS_DISCOVERED  = key("tess_discovered");

    }

    // ─────────────────────────────────────────────────────────────────────────
    // API publique
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sauvegarde toutes les données du joueur.
     * ▸ Données primitives → PDC (sur l'entité Player en ligne)
     * ▸ Données complexes  → YAML si non vides, sinon suppression du fichier
     *
     * @param rpg le joueur RPG à sauvegarder
     */
    public void save(RPGPlayer rpg) {
        Player player = rpg.getBukkitPlayer();
        if (player != null && player.isOnline()) {
            saveToPDC(rpg, player);
        }
        saveToYaml(rpg);
    }

    /**
     * Charge toutes les données du joueur depuis le PDC et le YAML (si existant).
     *
     * @param rpg    le joueur RPG à remplir
     * @param player le Bukkit Player (en ligne)
     */
    public void load(RPGPlayer rpg, Player player) {
        loadFromPDC(rpg, player);
        loadFromYaml(rpg);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PDC — Sauvegarde
    // ─────────────────────────────────────────────────────────────────────────

    private void saveToPDC(RPGPlayer rpg, Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        // Race
        if (rpg.getCurrentRace() != null) {
            pdc.set(KEY_RACE, PersistentDataType.STRING, rpg.getCurrentRace().name());
        }

        // Progression
        pdc.set(KEY_LEVEL,      PersistentDataType.DOUBLE, rpg.getLevel());
        pdc.set(KEY_EXPERIENCE, PersistentDataType.LONG,   rpg.getExperience());

        // Vitaux
        pdc.set(KEY_HEALTH,     PersistentDataType.DOUBLE, rpg.getHealth());
        pdc.set(KEY_MAX_HEALTH, PersistentDataType.DOUBLE, rpg.getMaxHealth());
        pdc.set(KEY_MANA,       PersistentDataType.DOUBLE, rpg.getMana());
        pdc.set(KEY_MAX_MANA,   PersistentDataType.DOUBLE, rpg.getMaxMana());
        pdc.set(KEY_FOOD,       PersistentDataType.DOUBLE, rpg.getFood());

        // Attributs principaux
        pdc.set(KEY_STRENGTH,     PersistentDataType.DOUBLE, rpg.getStrength());
        pdc.set(KEY_DEXTERITY,    PersistentDataType.DOUBLE, rpg.getDexterity());
        pdc.set(KEY_CONSTITUTION, PersistentDataType.DOUBLE, rpg.getConstitution());
        pdc.set(KEY_INTELLIGENCE, PersistentDataType.DOUBLE, rpg.getIntelligence());
        pdc.set(KEY_CHARISMA,     PersistentDataType.DOUBLE, rpg.getCharisma());

        // Combat
        pdc.set(KEY_CRIT_CHANCE,  PersistentDataType.FLOAT, rpg.getCritChance());
        pdc.set(KEY_CRIT_DAMAGE,  PersistentDataType.FLOAT, rpg.getCritDamage());
        pdc.set(KEY_DODGE_CHANCE, PersistentDataType.FLOAT, rpg.getDodgeChance());

        // Stats physiques
        pdc.set(KEY_MOVEMENT_SPEED,       PersistentDataType.DOUBLE, rpg.getMovementSpeed());
        pdc.set(KEY_ATTACK_DAMAGE,        PersistentDataType.DOUBLE, rpg.getAttackDamage());
        pdc.set(KEY_ATTACK_SPEED,         PersistentDataType.FLOAT,  rpg.getAttackSpeed());
        pdc.set(KEY_JUMP_STRENGTH,        PersistentDataType.FLOAT,  rpg.getJumpStrength());
        pdc.set(KEY_KNOCKBACK_RESISTANCE, PersistentDataType.FLOAT,  rpg.getKnockbackResistance());
        pdc.set(KEY_ARMOR,                PersistentDataType.FLOAT,  rpg.getArmor());
        pdc.set(KEY_TOUGHNESS,            PersistentDataType.FLOAT,  rpg.getToughness());

        // Résistances & capacités (boolean → BYTE : 1 = true, 0 = false)
        pdc.set(KEY_FALL_DAMAGE_REDUCTION,     PersistentDataType.DOUBLE, rpg.getFallDamageReduction());
        pdc.set(KEY_NO_FALL_DAMAGE,            PersistentDataType.BYTE,   boolToByte(rpg.isNoFallDamage()));
        pdc.set(KEY_SWIM_SPEED,               PersistentDataType.DOUBLE, rpg.getSwimSpeedMultiplier());
        pdc.set(KEY_MINING_FATIGUE_RESISTANCE, PersistentDataType.DOUBLE, rpg.getMiningFatigueResistance());
        pdc.set(KEY_POISON_RESISTANCE,        PersistentDataType.DOUBLE, rpg.getPoisonResistance());
        pdc.set(KEY_SUNLIGHT_WEAKNESS,        PersistentDataType.BYTE,   boolToByte(rpg.isSunlightWeakness()));
        pdc.set(KEY_WATER_BREATHING,          PersistentDataType.BYTE,   boolToByte(rpg.isWaterBreathing()));
        pdc.set(KEY_CAN_CLIMB_WALLS,          PersistentDataType.BYTE,   boolToByte(rpg.isCanClimbWalls()));

        // Économique
        pdc.set(KEY_EXP_BONUS,     PersistentDataType.DOUBLE, rpg.getExpBonus());
        pdc.set(KEY_LOOT_BONUS,    PersistentDataType.DOUBLE, rpg.getLootBonus());
        pdc.set(KEY_TRADE_DISCOUNT, PersistentDataType.DOUBLE, rpg.getTradeDiscount());

        // Skills
        pdc.set(KEY_SKILL_POINTS,           PersistentDataType.INTEGER, rpg.getSkillPoints());
        pdc.set(KEY_SKILL_POINTS_PER_LEVEL, PersistentDataType.INTEGER, rpg.getSkillPointsPerLevel());

        TessereLoadout loadout = rpg.getTessereLoadout();

// Map slot → clé PDC
        Map<TessereSlot, NamespacedKey> cleParSlot = Map.of(
                TessereSlot.HEAD,      KEY_TESS_HEAD,
                TessereSlot.CHEST,     KEY_TESS_CHEST,
                TessereSlot.RIGHT_ARM, KEY_TESS_RIGHT_ARM,
                TessereSlot.LEFT_ARM,  KEY_TESS_LEFT_ARM,
                TessereSlot.RIGHT_LEG, KEY_TESS_RIGHT_LEG,
                TessereSlot.LEFT_LEG,  KEY_TESS_LEFT_LEG
        );

        cleParSlot.forEach((slot, cle) -> {
            Tessere t = loadout.getEquippedFragments().get(slot);
            if (t != null && t.getType() != null)
                pdc.set(cle, PersistentDataType.STRING, t.getType().name());
            else
                pdc.remove(cle);
        });

        if (loadout.isRevealed() && !loadout.getActiveSkills().isEmpty()) {
            String csv = loadout.getActiveSkills().stream()
                    .filter(s -> s instanceof TessereSkill)
                    .map(s -> ((TessereSkill) s).name())
                    .collect(Collectors.joining(","));
            pdc.set(KEY_TESS_SKILLS,   PersistentDataType.STRING,  csv);
            pdc.set(KEY_TESS_SCORE,    PersistentDataType.INTEGER, (int)(loadout.getEfficaciteScore() * 1000));
            pdc.set(KEY_TESS_REVEALED, PersistentDataType.BYTE,    (byte) 1);
        }

        // Discovered Tesseres
        Set<fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType> discovered = loadout.getDiscoveredTesseres();
        if (!discovered.isEmpty()) {
            String discoveredCsv = discovered.stream()
                    .map(Enum::name).collect(Collectors.joining(","));
            pdc.set(KEY_TESS_DISCOVERED, PersistentDataType.STRING, discoveredCsv);
        } else {
            pdc.remove(KEY_TESS_DISCOVERED);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PDC — Chargement
    // ─────────────────────────────────────────────────────────────────────────

    private void loadFromPDC(RPGPlayer rpg, Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        // Race
        String raceName = pdc.get(KEY_RACE, PersistentDataType.STRING);
        if (raceName != null) {
            try {
                rpg.setCurrentRace(RaceType.valueOf(raceName));
            } catch (IllegalArgumentException e) {
                log.warning("[RPG] RaceType inconnue '" + raceName + "' pour " + player.getName() + " — ignorée.");
            }
        }

        // Progression
        rpg.setLevel(     orDefault(pdc, KEY_LEVEL,      PersistentDataType.DOUBLE, 1.0));
        rpg.setExperience(orDefault(pdc, KEY_EXPERIENCE, PersistentDataType.LONG,   0L));

        // Vitaux
        rpg.setHealth(   orDefault(pdc, KEY_HEALTH,     PersistentDataType.DOUBLE, 20.0));
        rpg.setMaxHealth(orDefault(pdc, KEY_MAX_HEALTH, PersistentDataType.DOUBLE, 20.0));
        rpg.setMana(     orDefault(pdc, KEY_MANA,       PersistentDataType.DOUBLE, 100.0));
        rpg.setMaxMana(  orDefault(pdc, KEY_MAX_MANA,   PersistentDataType.DOUBLE, 100.0));
        rpg.setFood(     orDefault(pdc, KEY_FOOD,       PersistentDataType.DOUBLE, 20.0));

        // Attributs
        rpg.setStrength(    orDefault(pdc, KEY_STRENGTH,     PersistentDataType.DOUBLE, 10.0));
        rpg.setDexterity(   orDefault(pdc, KEY_DEXTERITY,    PersistentDataType.DOUBLE, 10.0));
        rpg.setConstitution(orDefault(pdc, KEY_CONSTITUTION, PersistentDataType.DOUBLE, 10.0));
        rpg.setIntelligence(orDefault(pdc, KEY_INTELLIGENCE, PersistentDataType.DOUBLE, 10.0));
        rpg.setCharisma(    orDefault(pdc, KEY_CHARISMA,     PersistentDataType.DOUBLE, 10.0));

        // Combat
        rpg.setCritChance( orDefault(pdc, KEY_CRIT_CHANCE,  PersistentDataType.FLOAT, 0.05f));
        rpg.setCritDamage( orDefault(pdc, KEY_CRIT_DAMAGE,  PersistentDataType.FLOAT, 1.50f));
        rpg.setDodgeChance(orDefault(pdc, KEY_DODGE_CHANCE, PersistentDataType.FLOAT, 0.00f));

        // Physique
        rpg.setMovementSpeed(      orDefault(pdc, KEY_MOVEMENT_SPEED,       PersistentDataType.DOUBLE, 0.2));
        rpg.setAttackDamage(       orDefault(pdc, KEY_ATTACK_DAMAGE,        PersistentDataType.DOUBLE, 1.0));
        rpg.setAttackSpeed(        orDefault(pdc, KEY_ATTACK_SPEED,         PersistentDataType.FLOAT,  4.0f));
        rpg.setJumpStrength(       orDefault(pdc, KEY_JUMP_STRENGTH,        PersistentDataType.FLOAT,  0.42f));
        rpg.setKnockbackResistance(orDefault(pdc, KEY_KNOCKBACK_RESISTANCE, PersistentDataType.FLOAT,  0.0f));
        rpg.setArmor(              orDefault(pdc, KEY_ARMOR,                PersistentDataType.FLOAT,  0.0f));
        rpg.setToughness(          orDefault(pdc, KEY_TOUGHNESS,            PersistentDataType.FLOAT,  0.0f));

        // Résistances
        rpg.setFallDamageReduction(    orDefault(pdc, KEY_FALL_DAMAGE_REDUCTION,     PersistentDataType.DOUBLE, 0.0));
        rpg.setNoFallDamage(           orDefault(pdc, KEY_NO_FALL_DAMAGE,            PersistentDataType.BYTE,   (byte) 0) == 1);
        rpg.setSwimSpeedMultiplier(    orDefault(pdc, KEY_SWIM_SPEED,               PersistentDataType.DOUBLE, 1.0));
        rpg.setMiningFatigueResistance(orDefault(pdc, KEY_MINING_FATIGUE_RESISTANCE, PersistentDataType.DOUBLE, 0.0));
        rpg.setPoisonResistance(       orDefault(pdc, KEY_POISON_RESISTANCE,        PersistentDataType.DOUBLE, 0.0));
        rpg.setSunlightWeakness(       orDefault(pdc, KEY_SUNLIGHT_WEAKNESS,        PersistentDataType.BYTE,   (byte) 0) == 1);
        rpg.setWaterBreathing(         orDefault(pdc, KEY_WATER_BREATHING,          PersistentDataType.BYTE,   (byte) 0) == 1);
        rpg.setCanClimbWalls(          orDefault(pdc, KEY_CAN_CLIMB_WALLS,          PersistentDataType.BYTE,   (byte) 0) == 1);

        // Économique
        rpg.setExpBonus(    orDefault(pdc, KEY_EXP_BONUS,      PersistentDataType.DOUBLE, 0.0));
        rpg.setLootBonus(   orDefault(pdc, KEY_LOOT_BONUS,     PersistentDataType.DOUBLE, 0.0));
        rpg.setTradeDiscount(orDefault(pdc, KEY_TRADE_DISCOUNT, PersistentDataType.DOUBLE, 0.0));

        // Skills
        rpg.setSkillPoints(        orDefault(pdc, KEY_SKILL_POINTS,           PersistentDataType.INTEGER, 0));
        rpg.setSkillPointsPerLevel(orDefault(pdc, KEY_SKILL_POINTS_PER_LEVEL, PersistentDataType.INTEGER, 3));

        TessereLoadout loadout = rpg.getTessereLoadout();

        Map<TessereSlot, NamespacedKey> cleParSlot = Map.of(
                TessereSlot.HEAD,      KEY_TESS_HEAD,
                TessereSlot.CHEST,     KEY_TESS_CHEST,
                TessereSlot.RIGHT_ARM, KEY_TESS_RIGHT_ARM,
                TessereSlot.LEFT_ARM,  KEY_TESS_LEFT_ARM,
                TessereSlot.RIGHT_LEG, KEY_TESS_RIGHT_LEG,
                TessereSlot.LEFT_LEG,  KEY_TESS_LEFT_LEG
        );

        cleParSlot.forEach((slot, cle) -> {
            String nom = pdc.get(cle, PersistentDataType.STRING);
            if (nom == null) return;
            try {
                TessereType type = TessereType.valueOf(nom);
                loadout.getEquippedFragments().put(slot, type.buildTessere());
            } catch (IllegalArgumentException e) {
                log.warning("[RPG] TessereType inconnue : " + nom + " — ignorée.");
            }
        });

        // Discovered Tesseres
        String discoveredCsv = pdc.get(KEY_TESS_DISCOVERED, PersistentDataType.STRING);
        if (discoveredCsv != null && !discoveredCsv.isEmpty()) {
            Arrays.stream(discoveredCsv.split(",")).forEach(name -> {
                try { loadout.discover(TessereType.valueOf(name)); }
                catch (IllegalArgumentException e) {
                    log.warning("[RPG] Unknown discovered TessereType: " + name + " — ignored.");
                }
            });
        }

        if (orDefault(pdc, KEY_TESS_REVEALED, PersistentDataType.BYTE, (byte)0) == 1) {
            String csv   = pdc.get(KEY_TESS_SKILLS, PersistentDataType.STRING);
            int scoreBrut = orDefault(pdc, KEY_TESS_SCORE, PersistentDataType.INTEGER, 500);
            if (csv != null) {
                List<Skill> skills = Arrays.stream(csv.split(","))
                        .map(s -> { try { return (Skill) TessereSkill.valueOf(s); } catch (Exception e) { return null; } })
                        .filter(Objects::nonNull).toList();
                if (skills.size() == 3)
                    loadout.revealSkills(skills, 0, scoreBrut / 1000.0);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // YAML — Sauvegarde
    // ─────────────────────────────────────────────────────────────────────────

    private void saveToYaml(RPGPlayer rpg) {
        boolean hasEffects   = !rpg.getActiveEffects().isEmpty();
        boolean hasStatuses  = !rpg.getActiveStatuses().isEmpty();
        long    now          = System.currentTimeMillis();
        boolean hasCooldowns = rpg.getCooldowns().values().stream().anyMatch(ts -> ts > now);

        File file = getPlayerFile(rpg.getPlayerId());

        // Aucune donnée complexe → on supprime le fichier s'il existe
        if (!hasEffects && !hasStatuses && !hasCooldowns) {
            if (file.exists()) {
                if (!file.delete()) {
                    log.warning("[RPG] Impossible de supprimer le fichier YAML vide : " + file.getName());
                }
            }
            return;
        }

        YamlConfiguration yaml = new YamlConfiguration();

        // ── activeEffects ────────────────────────────────────────────────────
        if (hasEffects) {
            List<Map<String, Object>> effectList = new ArrayList<>();
            for (PotionEffect effect : rpg.getActiveEffects()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("type",      effect.getType().getName());
                map.put("duration",  effect.getDuration());
                map.put("amplifier", effect.getAmplifier());
                map.put("ambient",   effect.isAmbient());
                map.put("particles", effect.hasParticles());
                map.put("icon",      effect.hasIcon());
                effectList.add(map);
            }
            yaml.set("active-effects", effectList);
        }

        // ── activeStatuses ───────────────────────────────────────────────────
        // Sauvegarde par ID + displayName (BuffsEnum / DebuffsEnum actuellement vides,
        // structure prête pour quand ils implémenteront Status).
        if (hasStatuses) {
            List<Map<String, Object>> statusList = new ArrayList<>();
            for (Status status : rpg.getActiveStatuses()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id",     status.getID());
                map.put("name",   status.getDisplayName());
                map.put("stacks", status.getStacks());
                statusList.add(map);
            }
            yaml.set("active-statuses", statusList);
        }

        // ── cooldowns (uniquement les non-expirés) ────────────────────────────
        if (hasCooldowns) {
            Map<String, Object> cdMap = new LinkedHashMap<>();
            rpg.getCooldowns().forEach((name, ts) -> {
                if (ts > now) cdMap.put(name, ts);
            });
            yaml.set("cooldowns", cdMap);
        }

        try {
            if (!playersFolder.exists()) playersFolder.mkdirs();
            yaml.save(file);
        } catch (IOException e) {
            log.severe("[RPG] Échec sauvegarde YAML du joueur " + rpg.getPlayerId() + " : " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // YAML — Chargement
    // ─────────────────────────────────────────────────────────────────────────

    private void loadFromYaml(RPGPlayer rpg) {
        File file = getPlayerFile(rpg.getPlayerId());
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        // ── activeEffects ────────────────────────────────────────────────────
        rpg.getActiveEffects().clear();
        List<?> rawEffects = yaml.getList("active-effects");
        if (rawEffects != null) {
            for (Object obj : rawEffects) {
                if (!(obj instanceof Map<?, ?> map)) continue;

                String typeName = mapString(map, "type");
                if (typeName == null) continue;

                @SuppressWarnings("deprecation")
                PotionEffectType type = PotionEffectType.getByName(typeName);
                if (type == null) {
                    log.warning("[RPG] PotionEffectType inconnue '" + typeName + "' — ignorée.");
                    continue;
                }

                int     duration  = mapInt(map,  "duration",  200);
                int     amplifier = mapInt(map,  "amplifier", 0);
                boolean ambient   = mapBool(map, "ambient",   false);
                boolean particles = mapBool(map, "particles", true);
                boolean icon      = mapBool(map, "icon",      true);

                rpg.getActiveEffects().add(
                        new PotionEffect(type, duration, amplifier, ambient, particles, icon)
                );
            }
        }

        // ── activeStatuses ────────────────────────────────────────────────────
        // BuffsEnum et DebuffsEnum sont actuellement vides.
        // Quand ils implémenteront Status, ajouter ici la résolution par ID.
        rpg.getActiveStatuses().clear();

        // ── cooldowns (non-expirés seulement) ─────────────────────────────────
        rpg.getCooldowns().clear();
        ConfigurationSection cdSection = yaml.getConfigurationSection("cooldowns");
        if (cdSection != null) {
            long now = System.currentTimeMillis();
            for (String cdKey : cdSection.getKeys(false)) {
                long ts = cdSection.getLong(cdKey);
                if (ts > now) {
                    rpg.getCooldowns().put(cdKey, ts);
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilitaires internes
    // ─────────────────────────────────────────────────────────────────────────

    private NamespacedKey key(String name) {
        return new NamespacedKey(plugin, name);
    }

    private File getPlayerFile(UUID uuid) {
        return new File(playersFolder, uuid + ".yml");
    }

    /** Retourne la valeur PDC ou la valeur par défaut si la clé est absente. */
    private <T, Z> Z orDefault(PersistentDataContainer pdc, NamespacedKey k,
                                PersistentDataType<T, Z> type, Z def) {
        Z val = pdc.get(k, type);
        return val != null ? val : def;
    }

    private byte boolToByte(boolean b) {
        return b ? (byte) 1 : (byte) 0;
    }

    // Helpers pour lire une Map YAML de type inconnu
    private String mapString(Map<?, ?> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private int mapInt(Map<?, ?> map, String key, int def) {
        Object v = map.get(key);
        return v instanceof Number n ? n.intValue() : def;
    }

    private boolean mapBool(Map<?, ?> map, String key, boolean def) {
        Object v = map.get(key);
        return v instanceof Boolean b ? b : def;
    }
}
