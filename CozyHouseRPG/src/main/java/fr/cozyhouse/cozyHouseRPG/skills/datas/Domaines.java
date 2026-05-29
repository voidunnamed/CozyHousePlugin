package fr.cozyhouse.cozyHouseRPG.skills.datas;

import lombok.AllArgsConstructor;
import org.bukkit.Material;

@AllArgsConstructor
public enum Domaines {

    MATTER(Material.GUNPOWDER),
    ENERGY(Material.AMETHYST_SHARD),
    SPACE(Material.ENDER_PEARL),
    TIME(Material.CLOCK),
    LIFE(Material.TURTLE_EGG),
    AWARENESS(Material.WRITABLE_BOOK),
    INFORMATION(Material.OAK_SIGN),
    RELATIONSHIP(Material.NAME_TAG),
    EXISTENCE(Material.ALLAY_SPAWN_EGG),
    HARMONY(Material.BEACON),
    CHAOS(Material.SCULK),
    TRANSCENDENCE(Material.ENDER_EYE),
    ORIGINE(Material.BEDROCK);

    private final Material iconMaterial;
}
