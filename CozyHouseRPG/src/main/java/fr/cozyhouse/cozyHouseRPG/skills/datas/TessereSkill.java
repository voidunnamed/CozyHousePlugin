package fr.cozyhouse.cozyHouseRPG.skills.datas;

import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.SkillCategory;
import fr.cozyhouse.cozyHouseRPG.skills.SkillType;
import fr.cozyhouse.cozyHouseRPG.status.Status;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fr.cozyhouse.cozyHouseRPG.skills.SkillCategory.*;
import static fr.cozyhouse.cozyHouseRPG.skills.SkillCategory.MAGIC;
import static fr.cozyhouse.cozyHouseRPG.skills.SkillType.*;
import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;

/**
 * Compétences révélables via le système de Tessères.
 * Chaque valeur implémente l'interface Skill existante.
 *
 * param(clé, score) → valeur de base × (0.5 + score)
 *   score 0.0 → ×0.5 (gravure mauvaise)
 *   score 0.5 → ×1.0 (gravure moyenne)
 *   score 1.0 → ×1.5 (gravure parfaite)
 */
public enum TessereSkill implements Skill {

    // ─── Offensives ────────────────────────────────────────────────────────────

    MARQUE_ARDENTE(
            "Marque Ardente", PASSIVE, COMBAT, RED, BLAZE_ROD,
            "Tes frappes portent la chaleur du Thermique. Chance d'enflammer la cible.",
            0.30, Map.of("chance", 0.30, "duree_ticks", 60.0)
    ),
    IMPACT_CINETIQUE(
            "Impact Cinétique", PASSIVE, COMBAT, LIGHT_PURPLE, AMETHYST_SHARD,
            "La Cinétique amplifie chaque frappe. Knockback augmenté sur tous tes coups.",
            0.6, Map.of("puissance", 0.6)
    ),
    LAME_DU_VIDE(
            "Lame du Vide", PASSIVE, STEALTH, DARK_PURPLE, INK_SAC,
            "L'Occultation affûte ta lame dans l'ombre. +40% dégâts quand tu es accroupi.",
            0.40, Map.of("bonus", 0.40)
    ),
    DECLIN_FORCE(
            "Déclin de Force", TRIGGERED, COMBAT, DARK_GRAY, WITHER_ROSE,
            "L'Extinction ronge la puissance ennemie. Applique Faiblesse à chaque frappe.",
            0.0, Map.of("duree_ticks", 80.0)
    ),
    CHOC_DIMENSIONNEL(
            "Choc Dimensionnel", ACTIVE, MAGIC, DARK_AQUA, END_CRYSTAL,
            "La Dimension explose autour de toi. Dégâts en zone à l'activation.",
            4.0, Map.of("rayon", 3.0, "degats", 4.0, "cooldown_ticks", 300.0)
    ),

    // ─── Défensives ────────────────────────────────────────────────────────────

    PEAU_DE_PIERRE(
            "Peau de Pierre", PASSIVE, DEFENSE, GRAY, IRON_INGOT,
            "La Solidification imprègne ta chair. Armure passive permanente.",
            3.0, Map.of("bonus_armure", 3.0)
    ),
    ANCRAGE(
            "Ancrage", PASSIVE, DEFENSE, DARK_GRAY, STONE,
            "La Densité t'ancre au sol. Résistance au recul passive.",
            0.2, Map.of("bonus_kb", 0.2)
    ),
    REGENERATION_HP(
            "Régénération Vitale", PASSIVE, DEFENSE, GREEN, GOLDEN_APPLE,
            "La Régénération pulse en toi. Récupère des HP lentement en combat.",
            0.5, Map.of("hp_par_sec", 0.5)
    ),
    VITALITE_ELARGIE(
            "Vitalité Élargie", PASSIVE, DEFENSE, GREEN, GLISTERING_MELON_SLICE,
            "La Vitalité étend ta réserve vitale. HP maximum augmentés.",
            4.0, Map.of("bonus_hp", 4.0)
    ),
    BOUCLIER_LUMINE(
            "Bouclier de Lumière", PASSIVE, MAGIC, YELLOW, GLOWSTONE_DUST,
            "La Lumineuse forme un voile protecteur. Réduit les dégâts magiques reçus.",
            0.25, Map.of("reduction", 0.25)
    ),

    // ─── Mobilité ──────────────────────────────────────────────────────────────

    BOND_INSTANTANE(
            "Bond Instantané", ACTIVE, EXPLORATION, AQUA, ENDER_PEARL,
            "La Translation propulse ton corps vers l'avant en un instant.",
            1.2, Map.of("puissance", 1.2, "cooldown_ticks", 200.0)
    ),
    REFLEXES_ACCRUS(
            "Réflexes Accrus", PASSIVE, COMBAT, GOLD, CLOCK,
            "L'Accélération affûte tes réflexes. Vitesse d'attaque augmentée.",
            0.3, Map.of("bonus_attaque", 0.3)
    ),

    // ─── Utilitaires ───────────────────────────────────────────────────────────

    VUE_ARCANIQUE(
            "Vue Arcanique", PASSIVE, EXPLORATION, WHITE, SPYGLASS,
            "La Clarté ouvre tes yeux au-delà du visible. Vision nocturne permanente.",
            0.0, Map.of()
    ),
    INSTINCT_AFFINE(
            "Instinct Affiné", PASSIVE, COMBAT, AQUA, WRITABLE_BOOK,
            "La Perception affûte ton instinct de combat. Chance de coup critique augmentée.",
            0.08, Map.of("bonus_crit", 0.08)
    ),
    HARMONIE_INT(
            "Harmonie Intérieure", PASSIVE, MAGIC, WHITE, BEACON,
            "L'Équilibre harmonise tes forces. Légère amélioration de toutes les stats.",
            1.0, Map.of("bonus_stats", 1.0)
    ),
    CROISSANCE_ACCL(
            "Croissance Accélérée", PASSIVE, EXPLORATION, DARK_GREEN, WHEAT_SEEDS,
            "La Croissance accélère ton développement. XP gagné augmenté.",
            0.15, Map.of("bonus_xp", 0.15)
    ),
    DUREE_ETENDUE(
            "Durée Étendue", PASSIVE, MAGIC, WHITE, DAYLIGHT_DETECTOR,
            "La Durée étire le temps de tes effets. Les effets positifs durent plus longtemps.",
            1.5, Map.of("multiplicateur", 1.5)
    ),

    // ─── Spéciales ─────────────────────────────────────────────────────────────

    CHAOS_LATENT(
            "Chaos Latent", TRIGGERED, COMBAT, DARK_RED, SCULK,
            "L'Instabilité libère des effets imprévisibles. Multiplicateur de dégâts aléatoire.",
            0.20, Map.of("chance", 0.20, "mult_min", 0.5, "mult_max", 2.5)
    ),
    EVEIL_ARCANIQUE(
            "Éveil Arcanique", PASSIVE, MAGIC, GOLD, NETHER_STAR,
            "L'Ascension réveille ton potentiel caché. Toutes les stats augmentées de 10%.",
            0.10, Map.of("pct_boost", 0.10)
    ),

    // ─── Fallbacks (gravure sans combinaison claire) ────────────────────────────

    FLUX_I  ("Flux Arcanique I",   PASSIVE, MAGIC, DARK_GRAY, GRAY_DYE, "Légère amélioration.", 0.02, Map.of("bonus", 0.02)),
    FLUX_II ("Flux Arcanique II",  PASSIVE, MAGIC, DARK_GRAY, GRAY_DYE, "Légère amélioration.", 0.02, Map.of("bonus", 0.02)),
    FLUX_III("Flux Arcanique III", PASSIVE, MAGIC, DARK_GRAY, GRAY_DYE, "Légère amélioration.", 0.02, Map.of("bonus", 0.02));

    // ─── Champs ────────────────────────────────────────────────────────────────

    private final String displayName;
    private final SkillType type;
    private final SkillCategory category;
    private final ChatColor color;
    private final Material iconMaterial;
    private final String description;
    private final double baseValue;
    private final Map<String, Double> params;

    TessereSkill(String displayName, SkillType type, SkillCategory category,
                 ChatColor color, Material iconMaterial, String description,
                 double baseValue, Map<String, Double> params) {
        this.displayName  = displayName;
        this.type         = type;
        this.category     = category;
        this.color        = color;
        this.iconMaterial = iconMaterial;
        this.description  = description;
        this.baseValue    = baseValue;
        this.params       = params;
    }

    // ─── Méthodes utilitaires ──────────────────────────────────────────────────

    /**
     * Retourne un paramètre amplifié par le score d'efficacité de la gravure.
     * score 0.0 → ×0.5 · score 0.5 → ×1.0 · score 1.0 → ×1.5
     */
    public double param(String cle, double efficaciteScore) {
        return params.getOrDefault(cle, 0.0) * (0.5 + efficaciteScore);
    }

    /** Paramètre brut, sans modification par le score (utile pour les ranges min/max). */
    public double paramBrut(String cle) {
        return params.getOrDefault(cle, 0.0);
    }

    // ─── Implémentation de Skill ───────────────────────────────────────────────

    @Override public int getId()                        { return ordinal(); }
    @Override public String getName()                   { return name().toLowerCase(); }
    @Override public String getDisplayName()            { return displayName; }
    @Override public String getDescription()            { return description; }
    @Override public SkillType getType()                { return type; }
    @Override public SkillCategory getCategory()        { return category; }
    @Override public boolean isUnlocked()               { return true; }
    @Override public boolean isEnabled()                { return true; }
    @Override public int getMaxLevel()                  { return 1; }
    @Override public int getCurrentLevel()              { return 1; }
    @Override public ChatColor getColor()               { return color; }
    @Override public long getCooldownTicks()            { return params.getOrDefault("cooldown_ticks", 0.0).longValue(); }
    @Override public long getRemainingCooldownTicks()   { return 0; } // géré par RPGPlayer.cooldowns
    @Override public double getValue()                  { return baseValue; }
    @Override public double getValuePerLevel()          { return 0; }
    @Override public double getBonusAtLevel(int level)  { return baseValue; }
    @Override public List<Status> getGrantedStatuses()  { return Collections.emptyList(); }
    @Override public List<String> getUnlockedAtLevels() { return Collections.emptyList(); }
    @Override public boolean isPassive()                { return type == PASSIVE; }
    @Override public boolean hasLevels()                { return false; }
    @Override public boolean isToggleable()             { return type == TOGGLE; }
    @Override public boolean consumesItem()             { return false; }
    @Override public Material getConsumableItem()       { return null; }

    @Override
    public ItemStack getIcon() {
        ItemStack item = new ItemStack(iconMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(color + "" + ChatColor.BOLD + displayName);
        meta.setLore(List.of(ChatColor.GRAY + description));
        item.setItemMeta(meta);
        return item;
    }
}
