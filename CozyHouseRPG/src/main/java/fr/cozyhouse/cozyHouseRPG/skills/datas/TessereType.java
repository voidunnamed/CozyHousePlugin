package fr.cozyhouse.cozyHouseRPG.skills.datas;

import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import fr.cozyhouse.cozyHouseRPG.skills.logic.TessereCalcul;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Enum des 20 Tessères disponibles.
 * slotEfficiency[6] — efficacité par emplacement, indexé par TessereSlot.ordinal()
 *   HEAD=0, CHEST=1, RIGHT_ARM=2, LEFT_ARM=3, RIGHT_LEG=4, LEFT_LEG=5
 * raceAffinity[8] — affinité par race, indexé par RaceType.ordinal()
 *   HUMAN=0, ELF=1, DWARF=2, ORC=3, GOBLIN=4, FAIRY=5, BEAST=6, FISH=7
 * 1.0 = neutre · ≥1.3 = excellent · ≤0.7 = mauvais
 */
@Getter
public enum TessereType {

    // ─── Domaine ÉNERGIE ───────────────────────────────────────────────────────

    THERMIQUE(
            Domaines.ENERGY, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.ASCENDING, Domaines.ENERGY),
            Material.BLAZE_POWDER, ChatColor.RED,
            "Thermique",
            "L'essence de la chaleur. Se propage, consume, transforme.",
            "Mendel disait : le feu est une Tessère vivante — elle veut se propager.",
            new double[]{ 0.6, 0.8, 1.4, 1.3, 0.7, 0.7 },
            new double[]{ 1.0, 0.8, 1.3, 1.3, 1.0, 0.7, 1.2, 0.6 }
    ),
    CINETIQUE(
            Domaines.ENERGY, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.ENERGY),
            Material.AMETHYST_SHARD, ChatColor.LIGHT_PURPLE,
            "Cinétique",
            "La force du mouvement à l'état pur. Ce qui frappe, ce qui propulse.",
            "Chaque Orc est un fragment de Cinétique incarné.",
            new double[]{ 0.7, 0.9, 1.4, 1.3, 1.0, 0.9 },
            new double[]{ 1.0, 0.9, 1.2, 1.4, 1.1, 0.7, 1.3, 0.8 }
    ),
    LUMINEUSE(
            Domaines.ENERGY, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.ENERGY),
            Material.GLOWSTONE_DUST, ChatColor.YELLOW,
            "Lumineuse",
            "La lumière comme vecteur d'énergie. Révèle, amplifie, protège.",
            "Glenn appelait la Lumineuse la Tessère des mages fragiles. Il avait tort.",
            new double[]{ 1.3, 1.0, 0.9, 1.0, 0.7, 0.7 },
            new double[]{ 1.0, 1.2, 0.8, 0.7, 0.9, 1.5, 1.0, 0.9 }
    ),

    // ─── Domaine MATIÈRE ───────────────────────────────────────────────────────

    SOLIDIFICATION(
            Domaines.MATTER, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.MATTER),
            Material.IRON_INGOT, ChatColor.GRAY,
            "Solidification",
            "La Matière qui se durcit, qui résiste. La Tessère des bâtisseurs.",
            "Les Nains ont gravé la Solidification avant d'apprendre à parler.",
            new double[]{ 0.6, 1.4, 1.0, 1.0, 0.9, 0.9 },
            new double[]{ 1.0, 0.8, 1.4, 1.2, 0.8, 0.6, 1.1, 0.9 }
    ),
    DENSITE(
            Domaines.MATTER, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.DESCENDING, Domaines.MATTER),
            Material.STONE, ChatColor.DARK_GRAY,
            "Densité",
            "Plus lourd que l'air, plus stable que la roche.",
            "Celui qui porte la Densité ne trébuche pas.",
            new double[]{ 0.6, 1.0, 0.8, 0.8, 1.3, 1.4 },
            new double[]{ 1.0, 0.7, 1.3, 1.3, 0.7, 0.6, 1.1, 1.0 }
    ),

    // ─── Domaine ESPACE ────────────────────────────────────────────────────────

    TRANSLATION(
            Domaines.SPACE, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.SPACE),
            Material.ENDER_PEARL, ChatColor.AQUA,
            "Translation",
            "La position change. L'être reste. L'Espace ne fait que ça.",
            "Les Celestials voyageaient en Espace comme on marche dans un couloir.",
            new double[]{ 0.8, 0.7, 0.9, 0.9, 1.4, 1.4 },
            new double[]{ 1.0, 1.3, 0.8, 0.9, 1.4, 1.2, 1.2, 1.3 }
    ),
    DIMENSION(
            Domaines.SPACE, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.ASCENDING, Domaines.SPACE),
            Material.END_CRYSTAL, ChatColor.DARK_AQUA,
            "Dimension",
            "La taille de ce qui est. L'étendue d'une présence dans l'Espace.",
            "La Dimension, c'est la Tessère de la présence.",
            new double[]{ 1.0, 0.8, 1.2, 1.2, 0.9, 0.9 },
            new double[]{ 1.0, 1.1, 0.9, 1.0, 1.2, 1.3, 0.9, 1.0 }
    ),

    // ─── Domaine TEMPS ─────────────────────────────────────────────────────────

    ACCELERATION(
            Domaines.TIME, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.ASCENDING, Domaines.TIME),
            Material.CLOCK, ChatColor.GOLD,
            "Accélération",
            "Le Temps qui presse. Tout va plus vite — y compris tes réflexes.",
            "Un mage du Temps ne court pas. Il arrive avant.",
            new double[]{ 1.3, 0.7, 1.0, 1.0, 1.2, 1.0 },
            new double[]{ 1.0, 1.2, 0.9, 1.0, 1.4, 1.1, 1.1, 0.9 }
    ),
    DUREE(
            Domaines.TIME, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.TIME),
            Material.DAYLIGHT_DETECTOR, ChatColor.WHITE,
            "Durée",
            "Prolonge. Étire. Maintient ce qui devrait s'effacer.",
            "La Durée est la Tessère des patients.",
            new double[]{ 1.1, 1.2, 0.8, 0.9, 0.9, 0.9 },
            new double[]{ 1.1, 1.0, 1.0, 0.9, 1.0, 1.3, 0.9, 1.0 }
    ),

    // ─── Domaine VIE ───────────────────────────────────────────────────────────

    VITALITE(
            Domaines.LIFE, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.LIFE),
            Material.GLISTERING_MELON_SLICE, ChatColor.GREEN,
            "Vitalité",
            "L'élan de la vie. Ce qui bat, ce qui pulse, ce qui résiste.",
            "Mendel ne put jamais expliquer pourquoi certains ont plus de Vitalité que d'autres.",
            new double[]{ 0.8, 1.4, 0.9, 0.9, 1.0, 1.0 },
            new double[]{ 1.0, 1.1, 1.2, 1.1, 0.9, 0.9, 1.3, 1.0 }
    ),
    CROISSANCE(
            Domaines.LIFE, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.LIFE),
            Material.WHEAT_SEEDS, ChatColor.DARK_GREEN,
            "Croissance",
            "Ce qui était faible devient fort. La Tessère sans plafond.",
            "La Croissance est la seule Tessère qui ne connaît pas de plafond.",
            new double[]{ 0.9, 1.1, 0.9, 0.9, 1.2, 1.2 },
            new double[]{ 1.0, 1.2, 1.0, 1.0, 1.0, 1.1, 1.3, 1.0 }
    ),
    REGENERATION(
            Domaines.LIFE, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.LIFE),
            Material.GOLDEN_APPLE, ChatColor.GREEN,
            "Régénération",
            "La Vie qui se répare. La blessure qui se referme d'elle-même.",
            "Les Bêtes régénèrent sans le savoir. Les mages le font avec intention.",
            new double[]{ 0.9, 1.4, 0.7, 0.7, 1.0, 1.0 },
            new double[]{ 1.0, 1.1, 1.1, 1.0, 0.9, 1.2, 1.3, 1.1 }
    ),

    // ─── Domaine CONSCIENCE ────────────────────────────────────────────────────

    CLARTE(
            Domaines.AWARENESS, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.AWARENESS),
            Material.SPYGLASS, ChatColor.WHITE,
            "Clarté",
            "La perception aiguisée. Voir ce que les autres ne voient pas.",
            "Glenn gravait toujours Clarté en premier sur ses apprentis.",
            new double[]{ 1.5, 1.0, 0.7, 0.7, 0.7, 0.7 },
            new double[]{ 1.1, 1.4, 0.9, 0.7, 1.2, 1.3, 0.8, 1.0 }
    ),
    PERCEPTION(
            Domaines.AWARENESS, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.AWARENESS),
            Material.WRITABLE_BOOK, ChatColor.AQUA,
            "Perception",
            "Les sens au-delà du normal. Sentir avant de voir.",
            "Un Elfe avec Perception gravée entend les pensées, presque.",
            new double[]{ 1.4, 0.9, 1.0, 1.0, 0.8, 0.8 },
            new double[]{ 1.1, 1.3, 0.9, 0.8, 1.3, 1.2, 1.0, 1.0 }
    ),

    // ─── Domaine HARMONIE ──────────────────────────────────────────────────────

    EQUILIBRE(
            Domaines.HARMONY, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.HARMONY),
            Material.BEACON, ChatColor.WHITE,
            "Équilibre",
            "Ni trop fort, ni trop faible. La Tessère de ceux qui durent.",
            "Les Humains comprennent l'Équilibre instinctivement.",
            new double[]{ 1.1, 1.2, 1.0, 1.0, 1.0, 1.0 },
            new double[]{ 1.4, 1.1, 1.0, 0.8, 1.0, 1.2, 0.9, 1.1 }
    ),
    COHESION(
            Domaines.HARMONY, TessereRarity.BRUT,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.HARMONY),
            Material.NAME_TAG, ChatColor.AQUA,
            "Cohésion",
            "Le lien entre les êtres. Ce qui unit les Tessères entre elles.",
            "La Cohésion n'existe que quand plusieurs la cherchent ensemble.",
            new double[]{ 1.2, 1.2, 0.9, 0.9, 0.9, 0.9 },
            new double[]{ 1.3, 1.0, 1.0, 0.8, 1.0, 1.3, 0.9, 1.0 }
    ),

    // ─── Domaine CHAOS ─────────────────────────────────────────────────────────

    INSTABILITE(
            Domaines.CHAOS, TessereRarity.FACETTE,
            new TessereCalcul(TessereCalcul.Charge.NEGATIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.DESCENDING, Domaines.CHAOS),
            Material.SCULK, ChatColor.DARK_RED,
            "Instabilité",
            "Le Chaos gravé dans la chair. Imprévisible. Dangereux.",
            "Les Orcs gravent l'Instabilité par bravade. Certains survivent.",
            new double[]{ 0.9, 0.9, 1.2, 1.3, 1.0, 1.0 },
            new double[]{ 0.9, 0.8, 1.1, 1.4, 1.2, 0.7, 1.4, 0.9 }
    ),

    // ─── Domaine EXISTENCE ─────────────────────────────────────────────────────

    EXTINCTION(
            Domaines.EXISTENCE, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.NEGATIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.DESCENDING, Domaines.EXISTENCE),
            Material.WITHER_ROSE, ChatColor.DARK_GRAY,
            "Extinction",
            "Ce qui diminue, s'efface, s'oublie progressivement.",
            "L'Extinction n'est pas la Mort. C'est pire — c'est l'oubli progressif.",
            new double[]{ 0.9, 0.8, 1.3, 1.3, 0.9, 0.9 },
            new double[]{ 0.9, 0.8, 1.0, 1.3, 1.1, 0.7, 1.2, 0.9 }
    ),
    OCCULTATION(
            Domaines.EXISTENCE, TessereRarity.TAILLE,
            new TessereCalcul(TessereCalcul.Charge.NEGATIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.DESCENDING, Domaines.EXISTENCE),
            Material.INK_SAC, ChatColor.DARK_PURPLE,
            "Occultation",
            "L'absence de présence. Être là sans être perçu.",
            "Le meilleur assassin est celui qui n'existe pas.",
            new double[]{ 1.2, 0.7, 1.0, 1.2, 1.1, 1.1 },
            new double[]{ 0.9, 1.0, 0.8, 0.9, 1.4, 0.9, 1.1, 1.0 }
    ),

    // ─── Domaine TRANSCENDANCE ─────────────────────────────────────────────────

    ASCENSION(
            Domaines.TRANSCENDENCE, TessereRarity.ECLAT,
            new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.TRANSCENDENCE),
            Material.NETHER_STAR, ChatColor.GOLD,
            "Ascension",
            "Le dépassement de soi. La Tessère que les Celestials gardaient pour eux.",
            "Très peu maîtrisent l'Ascension. Ceux qui y arrivent ne reviennent pas inchangés.",
            new double[]{ 1.3, 1.2, 1.0, 1.0, 1.1, 1.1 },
            new double[]{ 1.1, 1.2, 1.0, 0.9, 1.0, 1.5, 1.0, 1.0 }
    );

    // ─── Champs ────────────────────────────────────────────────────────────────

    private final Domaines domaine;
    private final TessereRarity rarity;
    private final TessereCalcul calcul;
    private final Material iconMaterial;
    private final ChatColor color;
    private final String displayName;
    private final String description;
    private final String loreQuote;
    private final double[] slotEfficiency;
    private final double[] raceAffinity;

    TessereType(Domaines domaine, TessereRarity rarity, TessereCalcul calcul,
                Material iconMaterial, ChatColor color,
                String displayName, String description, String loreQuote,
                double[] slotEfficiency, double[] raceAffinity) {
        this.domaine       = domaine;
        this.rarity        = rarity;
        this.calcul        = calcul;
        this.iconMaterial  = iconMaterial;
        this.color         = color;
        this.displayName   = displayName;
        this.description   = description;
        this.loreQuote     = loreQuote;
        this.slotEfficiency = slotEfficiency;
        this.raceAffinity  = raceAffinity;
    }

    // ─── Méthodes ──────────────────────────────────────────────────────────────

    /** Efficacité de cette Tessère dans un slot donné. */
    public double getSlotEfficiency(TessereSlot slot) {
        return slotEfficiency[slot.ordinal()];
    }

    /** Affinité de cette Tessère pour une race donnée. */
    public double getRaceAffinity(RaceType race) {
        return raceAffinity[race.ordinal()];
    }

    /** Construit l'objet Tessere (display) correspondant à ce TessereType. */
    public Tessere buildTessere() {
        return Tessere.builder()
                .type(this)
                .rarity(this.rarity)
                .DisplayName(this.displayName)
                .Description(this.description)
                .LoreQuote(this.loreQuote)
                .color(this.color)
                .tessereCalcul(this.calcul)
                .iconMaterial(this.iconMaterial)
                .build();
    }

    /**
     * Label coloré pour afficher l'efficacité dans le lore.
     * Les libellés sont configurés dans stringMessages.yml (tessere.gui.eff-*).
     */
    public static String labelEff(double v, fr.cozyhouse.cozyHouse.MessageManager msg) {
        String pct = String.format("%.0f%%", v * 100);
        if (v >= 1.3) return msg.getMessageNoPrefix(
                fr.cozyhouse.cozyHouse.messages.MessagesEnum.TESSERE.GUI.EFF_EXCELLENT.getPath(), "%value%", pct);
        if (v >= 1.0) return msg.getMessageNoPrefix(
                fr.cozyhouse.cozyHouse.messages.MessagesEnum.TESSERE.GUI.EFF_GOOD.getPath(), "%value%", pct);
        return msg.getMessageNoPrefix(
                fr.cozyhouse.cozyHouse.messages.MessagesEnum.TESSERE.GUI.EFF_WEAK.getPath(), "%value%", pct);
    }
}
