package fr.cozyhouse.cozyHouseRPG.core;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.api.CozyHouseAPI;
import fr.cozyhouse.cozyHouseRPG.CozyHouseRPG;
import fr.cozyhouse.cozyHouseRPG.player.PlayerDataManager;
import fr.cozyhouse.cozyHouseRPG.player.PlayerRepository;
import fr.cozyhouse.cozyHouseRPG.player.PlayerService;
import fr.cozyhouse.cozyHouseRPG.race.RaceRegistry;
import fr.cozyhouse.cozyHouseRPG.skills.logic.SkillModeService;
import fr.cozyhouse.cozyHouseRPG.skills.logic.TessereService;
import fr.cozyhouse.cozyHouseRPG.utils.BiomeChecker;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

/**
 * Central context holding all plugin dependencies.
 * Les messages sont gérés par CozyHouse via CozyHouseAPI.getMessageManager().
 */
@Getter
public final class PluginContext {

    private final CozyHouseRPG plugin;
    private final Server server;
    private final PluginManager pluginManager;

    /** MessageManager de CozyHouse — source unique de vérité pour tous les messages. */
    private final MessageManager messageManager;

    private final RaceRegistry raceRegistry;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    /** Gestionnaire de persistance — PDC + YAML par joueur. */
    private final PlayerDataManager playerDataManager;

    /** BiomeChecker — centralisé ici pour être accessible aux listeners. */
    private final BiomeChecker biomeChecker;

    private final TessereService tessereService;
    private final SkillModeService skillModeService;

    /**
     * Initialise le contexte du plugin avec tous les services.
     * Doit être appelé une seule fois lors du onEnable.
     *
     * @param plugin l'instance principale du plugin
     */
    public PluginContext(CozyHouseRPG plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.pluginManager = server.getPluginManager();

        // Récupération du MessageManager de CozyHouse (chargé en premier via depend:)
        this.messageManager = CozyHouseAPI.getMessageManager();

        // Services RPG
        this.raceRegistry = new RaceRegistry(this);
        this.playerRepository = new PlayerRepository(this);
        this.playerDataManager = new PlayerDataManager(plugin);
        this.playerService = new PlayerService(this);

        // Chargement des données
        this.raceRegistry.loadAllRaces();

        // Démarrage du BiomeChecker (scheduler Bukkit — tourne toutes les secondes)
        this.biomeChecker = new BiomeChecker();
        this.biomeChecker.startChecking(plugin, this);

        this.tessereService    = new TessereService(this);
        this.skillModeService  = new SkillModeService(this);
    }

    /**
     * Arrête proprement tous les services.
     * Doit être appelé lors du onDisable.
     */
    public void shutdown() {
        biomeChecker.clear();
        playerRepository.saveAll();
        playerRepository.clear();
    }
}
