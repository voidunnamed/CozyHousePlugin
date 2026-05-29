package fr.cozyhouse.cozyHouseQuests.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Logger log;
    private FileConfiguration config;

    private static final String DEFAULT_PREFIX          = "&8[&6Quest&8] &r";
    private static final boolean DEFAULT_DEBUG          = false;
    private static final boolean DEFAULT_ACTIONBAR      = true;
    private static final boolean DEFAULT_BOSSBAR        = true;
    private static final int DEFAULT_SAVE_INTERVAL      = 300; // secondes
    private static final String DEFAULT_STORAGE         = "yaml"; //

    public ConfigManager(JavaPlugin plugin){
        this.plugin = plugin;
        this.log = plugin.getLogger();
    }

    public void load() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        validate();
        log.info("[ConfigManager] Configuration chargée.");
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        validate();
        log.info("[ConfigManager] Configuration rechargée.");
    }

    private void validate() {
        String storage = getStorageType();
        if (!storage.equals("yaml")) {
            log.warning("[ConfigManager] 'storage.type' invalide (" + storage
                    + "). Utilisation de 'yaml' par défaut.");
        }
        if (getAutoSaveInterval() < 60) {
            log.warning("[ConfigManager] 'storage.auto_save_interval' < 60s — risque de lag. Valeur recommandée : 300.");
        }
    }


    public String getPrefix() {
        return config.getString("messages.prefix", DEFAULT_PREFIX);
    }

    public boolean isDebugMode() {
        return config.getBoolean("debug", DEFAULT_DEBUG);
    }

    public boolean isActionBarEnabled() {
        return config.getBoolean("ui.action_bar", DEFAULT_ACTIONBAR);
    }

    public boolean isBossBarEnabled() {
        return config.getBoolean("ui.boss_bar", DEFAULT_BOSSBAR);
    }

    public int getAutoSaveInterval() {
        return config.getInt("storage.auto_save_interval", DEFAULT_SAVE_INTERVAL);
    }

    public String getStorageType() {
        return config.getString("storage.type", DEFAULT_STORAGE).toLowerCase();
    }

    public String getMessage(String key) {
        return config.getString("messages." + key, "&cMessage manquant : " + key);
    }

    /** Retourne le FileConfiguration brut pour des accès personnalisés. */
    public FileConfiguration getRaw() {
        return config;
    }
}
