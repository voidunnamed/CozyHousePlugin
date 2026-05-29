package fr.cozyhouse.cozyHouseRPG.player;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.attributes.AttributeApplicator;
import fr.cozyhouse.cozyHouseRPG.race.Race;
import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for player operations
 * Contains all business logic for player management
 * Coordinates between repository, races, and attributes
 */
public class PlayerService {

    private final PluginContext context;
    private final PlayerRepository repository;
    private final AttributeApplicator attributeApplicator;

    public PlayerService(PluginContext context) {
        this.context = context;
        this.repository = context.getPlayerRepository();
        this.attributeApplicator = new AttributeApplicator();
    }

    /**
     * Initializes a player when they join
     * Creates new player data or loads existing
     *
     * @param bukkitPlayer the Bukkit player
     * @return the initialized RPG player
     */
    public RPGPlayer initializePlayer(Player bukkitPlayer) {
        RPGPlayer rpgPlayer = repository.getOrCreate(bukkitPlayer.getUniqueId());
        rpgPlayer.setBukkitPlayer(bukkitPlayer);

        // Chargement des données persistées (PDC + YAML)
        context.getPlayerDataManager().load(rpgPlayer, bukkitPlayer);

        if (rpgPlayer.getTessereLoadout().isRevealed()) {
            context.getTessereService().appliquerEffetsPassifs(rpgPlayer);
        }

        // Si le joueur n'a pas encore de race (première connexion ou PDC vide)
        if (!rpgPlayer.hasRace()) {
            // La sélection de race sera gérée par le GUI
            return rpgPlayer;
        }

        // Ré-applique les attributs Bukkit à partir des stats chargées
        applyRaceStats(rpgPlayer);
        return rpgPlayer;
    }

    /**
     * Sets a player's race and applies all race stats
     *
     * @param rpgPlayer the RPG player
     * @param raceType the race to assign
     */
    public void setPlayerRace(RPGPlayer rpgPlayer, RaceType raceType) {
        Race race = context.getRaceRegistry().getRace(raceType);
        rpgPlayer.setCurrentRace(raceType);
        applyRaceStats(rpgPlayer, race);
    }

    /**
     * Applies race stats to player
     * Uses current race from player data
     *
     * @param rpgPlayer the player to update
     */
    public void applyRaceStats(RPGPlayer rpgPlayer) {
        if (!rpgPlayer.hasRace()) {
            throw new IllegalStateException("Player has no race");
        }
        
        Race race = context.getRaceRegistry().getRace(rpgPlayer.getCurrentRace());
        applyRaceStats(rpgPlayer, race);
    }

    /**
     * Applies specific race stats to player
     *
     * @param rpgPlayer the player to update
     * @param race the race stats to apply
     */
    private void applyRaceStats(RPGPlayer rpgPlayer, Race race) {
        // Copy base stats from race to player
        rpgPlayer.setMaxHealth(race.getBaseHealth());
        rpgPlayer.setHealth(race.getBaseHealth());
        rpgPlayer.setFood(20);
        
        rpgPlayer.setStrength(race.getBaseStrength());
        rpgPlayer.setDexterity(race.getBaseDexterity());
        rpgPlayer.setConstitution(race.getBaseConstitution());
        rpgPlayer.setIntelligence(race.getBaseIntelligence());
        rpgPlayer.setCharisma(race.getBaseCharisma());
        
        rpgPlayer.setCritChance(race.getBaseCritChance());
        rpgPlayer.setCritDamage(race.getBaseCritDamage());
        rpgPlayer.setDodgeChance(race.getBaseDodgeChance());
        
        rpgPlayer.setMovementSpeed(race.getBaseMovementSpeed());
        rpgPlayer.setAttackDamage(race.getBaseAttackDamage());
        rpgPlayer.setAttackSpeed(race.getBaseAttackSpeed());
        rpgPlayer.setJumpStrength(race.getBaseJumpStrength());
        rpgPlayer.setKnockbackResistance(race.getBaseKnockbackResistance());
        rpgPlayer.setArmor(race.getBaseArmor());
        rpgPlayer.setToughness(race.getBaseToughness());
        
        rpgPlayer.setFallDamageReduction(race.getFallDamageReduction());
        rpgPlayer.setNoFallDamage(race.isNoFallDamage());
        rpgPlayer.setSwimSpeedMultiplier(race.getSwimSpeedMultiplier());
        rpgPlayer.setMiningFatigueResistance(race.getMiningFatigueResistance());
        rpgPlayer.setPoisonResistance(race.getPoisonResistance());
        
        rpgPlayer.setSunlightWeakness(race.isSunlightWeakness());
        rpgPlayer.setWaterBreathing(race.isWaterBreathing());
        rpgPlayer.setCanClimbWalls(race.isCanClimbWalls());
        
        rpgPlayer.setExpBonus(race.getExpBonus());
        rpgPlayer.setLootBonus(race.getLootBonus());
        rpgPlayer.setTradeDiscount(race.getTradeDiscount());
        
        // Apply stats to Bukkit player
        if (rpgPlayer.getBukkitPlayer() != null && rpgPlayer.getBukkitPlayer().isOnline()) {
            attributeApplicator.applyAttributes(rpgPlayer.getBukkitPlayer(), rpgPlayer);
        }
    }

    /**
     * Refreshes player attributes
     * Re-applies all stats to Bukkit player
     *
     * @param rpgPlayer the player to refresh
     */
    public void refreshAttributes(RPGPlayer rpgPlayer) {
        if (rpgPlayer.getBukkitPlayer() != null && rpgPlayer.getBukkitPlayer().isOnline()) {
            attributeApplicator.applyAttributes(rpgPlayer.getBukkitPlayer(), rpgPlayer);
        }
    }

    /**
     * Gets a player by UUID
     *
     * @param playerId the player's UUID
     * @return optional containing the player if found
     */
    public Optional<RPGPlayer> getPlayer(UUID playerId) {
        return repository.findById(playerId);
    }

    /**
     * Gets a player by Bukkit player
     *
     * @param bukkitPlayer the Bukkit player
     * @return optional containing the player if found
     */
    public Optional<RPGPlayer> getPlayer(Player bukkitPlayer) {
        return repository.findByPlayer(bukkitPlayer);
    }

    /**
     * Removes a player when they disconnect
     *
     * @param playerId the player's UUID
     */
    public void removePlayer(UUID playerId) {
        repository.remove(playerId);
    }

}
