package fr.cozyhouse.cozyHouseRPG.race;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

/**
 * Enumeration of all available race types
 * Each race type has display metadata
 */
@Getter
@RequiredArgsConstructor
public enum RaceType {
    HUMAN("Humain", Material.IRON_SWORD, "Un aventurier polyvalent"),
    ELF("Elfe", Material.BOW, "Un archer agile de la forêt"),
    DWARF("Nain", Material.IRON_PICKAXE, "Un forgeron robuste des montagnes"),
    ORC("Orc", Material.IRON_AXE, "Un guerrier brutal et fort"),
    GOBLIN("Gobelin", Material.GOLD_NUGGET, "Une créature rusée et rapide"),
    FAIRY("Fée", Material.FEATHER, "Une créature magique ailée"),
    BEAST("Bête", Material.LEATHER, "Une créature sauvage et puissante"),
    FISH("Poisson", Material.COD, "Un être aquatique mystérieux");

    private final String displayName;
    private final Material iconMaterial;
    private final String shortDescription;
}
