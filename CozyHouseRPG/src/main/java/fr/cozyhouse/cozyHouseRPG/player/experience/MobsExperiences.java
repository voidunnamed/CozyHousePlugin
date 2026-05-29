package fr.cozyhouse.cozyHouseRPG.player.experience;

import lombok.AllArgsConstructor;
import org.bukkit.entity.EntityType;

@AllArgsConstructor
public enum MobsExperiences {

    // ── Passifs ──────────────────────────────────────────────────────────────
    ALLAY(EntityType.ALLAY, 20L, 10L),
    ARMADILLO(EntityType.ARMADILLO, 20L, 10L),
    AXOLOTL(EntityType.AXOLOTL, 20L, 10L),
    BAT(EntityType.BAT, 20L, 10L),
    CAMEL(EntityType.CAMEL, 20L, 10L),
    CAT(EntityType.CAT, 20L, 10L),
    CHICKEN(EntityType.CHICKEN, 20L, 10L),
    COD(EntityType.COD, 20L, 10L),
    COW(EntityType.COW, 20L, 10L),
    DONKEY(EntityType.DONKEY, 20L, 10L),
    FOX(EntityType.FOX, 20L, 10L),
    FROG(EntityType.FROG, 20L, 10L),
    GLOW_SQUID(EntityType.GLOW_SQUID, 20L, 10L),
    HAPPY_GHAST(EntityType.HAPPY_GHAST, 20L, 10L),
    GOAT(EntityType.GOAT, 20L, 10L),
    HORSE(EntityType.HORSE, 20L, 10L),
    LLAMA(EntityType.LLAMA, 20L, 10L),
    MOOSHROOM(EntityType.MOOSHROOM, 20L, 10L),
    MULE(EntityType.MULE, 20L, 10L),
    OCELOT(EntityType.OCELOT, 20L, 10L),
    PANDA(EntityType.PANDA, 20L, 10L),
    PARROT(EntityType.PARROT, 20L, 10L),
    PIG(EntityType.PIG, 20L, 10L),
    POLAR_BEAR(EntityType.POLAR_BEAR, 20L, 10L),
    PUFFERFISH(EntityType.PUFFERFISH, 20L, 10L),
    RABBIT(EntityType.RABBIT, 20L, 10L),
    SALMON(EntityType.SALMON, 20L, 10L),
    SHEEP(EntityType.SHEEP, 20L, 10L),
    SKELETON_HORSE(EntityType.SKELETON_HORSE, 20L, 10L),
    SNIFFER(EntityType.SNIFFER, 20L, 10L),
    SNOW_GOLEM(EntityType.SNOW_GOLEM, 20L, 10L),
    SQUID(EntityType.SQUID, 20L, 10L),
    STRIDER(EntityType.STRIDER, 20L, 10L),
    TADPOLE(EntityType.TADPOLE, 20L, 10L),
    TRADER_LLAMA(EntityType.TRADER_LLAMA, 20L, 10L),
    TROPICAL_FISH(EntityType.TROPICAL_FISH, 20L, 10L),
    TURTLE(EntityType.TURTLE, 20L, 10L),
    VILLAGER(EntityType.VILLAGER, 20L, 10L),
    WANDERING_TRADER(EntityType.WANDERING_TRADER, 20L, 10L),
    WOLF(EntityType.WOLF, 20L, 10L),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, 20L, 10L),

    // ── Neutres ──────────────────────────────────────────────────────────────
    BEE(EntityType.BEE, 20L, 10L),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, 20L, 10L),
    DOLPHIN(EntityType.DOLPHIN, 20L, 10L),
    ENDERMAN(EntityType.ENDERMAN, 20L, 10L),
    IRON_GOLEM(EntityType.IRON_GOLEM, 20L, 10L),
    PIGLIN(EntityType.PIGLIN, 20L, 10L),
    SPIDER(EntityType.SPIDER, 20L, 10L),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, 20L, 10L),
    ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, 20L, 10L),

    // ── Hostiles ─────────────────────────────────────────────────────────────
    BLAZE(EntityType.BLAZE, 20L, 10L),
    BOGGED(EntityType.BOGGED, 20L, 10L),
    BREEZE(EntityType.BREEZE, 20L, 10L),
    CREAKING(EntityType.CREAKING, 20L, 10L),
    CREEPER(EntityType.CREEPER, 20L, 10L),
    DROWNED(EntityType.DROWNED, 20L, 10L),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, 20L, 10L),
    ENDERMITE(EntityType.ENDERMITE, 20L, 10L),
    EVOKER(EntityType.EVOKER, 20L, 10L),
    GHAST(EntityType.GHAST, 20L, 10L),
    GIANT(EntityType.GIANT, 20L, 10L),
    GUARDIAN(EntityType.GUARDIAN, 20L, 10L),
    HOGLIN(EntityType.HOGLIN, 20L, 10L),
    HUSK(EntityType.HUSK, 20L, 10L),
    ILLUSIONER(EntityType.ILLUSIONER, 20L, 10L),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, 20L, 10L),
    PHANTOM(EntityType.PHANTOM, 20L, 10L),
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, 20L, 10L),
    PILLAGER(EntityType.PILLAGER, 20L, 10L),
    RAVAGER(EntityType.RAVAGER, 20L, 10L),
    SHULKER(EntityType.SHULKER, 20L, 10L),
    SILVERFISH(EntityType.SILVERFISH, 20L, 10L),
    SKELETON(EntityType.SKELETON, 20L, 10L),
    SLIME(EntityType.SLIME, 20L, 10L),
    STRAY(EntityType.STRAY, 20L, 10L),
    VEX(EntityType.VEX, 20L, 10L),
    VINDICATOR(EntityType.VINDICATOR, 20L, 10L),
    WARDEN(EntityType.WARDEN, 20L, 10L),
    WITCH(EntityType.WITCH, 20L, 10L),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, 20L, 10L),
    ZOGLIN(EntityType.ZOGLIN, 20L, 10L),
    ZOMBIE(EntityType.ZOMBIE, 20L, 10L),

    // ── Boss ─────────────────────────────────────────────────────────────────
    ENDER_DRAGON(EntityType.ENDER_DRAGON, 20L, 10L),
    WITHER(EntityType.WITHER, 20L, 10L);

    public final EntityType type;
    public final long max;
    public final long min;
}
