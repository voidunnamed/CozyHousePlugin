package fr.cozyhouse.cozyHouseQuests.manager;

import fr.cozyhouse.cozyHouseQuests.CozyHouseQuests;
import fr.cozyhouse.cozyHouseQuests.enums.QuestStatus;
import fr.cozyhouse.cozyHouseQuests.model.PlayerQuestData;
import fr.cozyhouse.cozyHouseQuests.model.PlayerQuestEntry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerDataManager {

    private final CozyHouseQuests plugin;
    private final Logger log;
    private final File playersFolder;


    private final Map<UUID, PlayerQuestData> cache = new HashMap<>();

    public PlayerDataManager(CozyHouseQuests plugin){
        this.plugin = plugin;
        this.log = plugin.getLogger();
        this.playersFolder = new File(plugin.getDataFolder(), "players");

        if (!playersFolder.exists() && !playersFolder.mkdirs()){
            log.severe("[PlayerDataManager] Impossible de créer le dossier players/");
        }
    }

    public void startAutoSave(int intervalSeconds){
        long ticks = intervalSeconds * 20L;
        new BukkitRunnable(){
            @Override
            public void run(){
                saveAll();
            }
        }.runTaskTimerAsynchronously(plugin, ticks, ticks);

        log.info("[PlayerDataManager] Auto-save activé toutes les " + intervalSeconds + "s.");
    }

    public void load(UUID uuid) {
        File file = fileFor(uuid);

        if (!file.exists()) {
            cache.put(uuid, new PlayerQuestData(uuid));
            log.info("[PlayerDataManager] Nouveau joueur : " + uuid);
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerQuestData data = new PlayerQuestData(uuid);

        if (config.isConfigurationSection("quests")) {
            for (String questId : config.getConfigurationSection("quests").getKeys(false)) {
                String path = "quests." + questId;
                String statusStr = config.getString(path + ".status", "IN_PROGRESS");
                String currentStage = config.getString(path + ".current_stage", "start");
                long completedAt = config.getLong(path + ".completed_at", 0);

                QuestStatus status;

                try {
                    status = QuestStatus.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    log.warning("[PlayerDataManager] Status inconnu '" + statusStr
                            + "' pour " + uuid + "/" + questId + " — ignoré.");
                    continue;
                }

                Map<String, Integer> progress = new HashMap<>();
                String objPath = path + ".objectives";
                if (config.isConfigurationSection(objPath)) {
                    for (String objId : config.getConfigurationSection(objPath).getKeys(false)) {
                        progress.put(objId, config.getInt(objPath + "." + objId, 0));
                    }
                }

                data.putEntry(questId, new PlayerQuestEntry(status, currentStage, progress, completedAt));
            }
        }

        cache.put(uuid, data);
        log.info("[PlayerDataManager] Données chargées pour " + uuid
                + " (" + data.getEntries().size() + " quête(s))");
    }

    public void unload(UUID uuid) {
        PlayerQuestData data = cache.remove(uuid);
        if (data == null) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                saveToFile(data);
            }
        }.runTaskAsynchronously(plugin);
    }

    public PlayerQuestData get(UUID uuid) {
        return cache.get(uuid);
    }

    public boolean isLoaded(UUID uuid) {
        return cache.containsKey(uuid);
    }

    public void saveAsync(UUID uuid) {
        PlayerQuestData data = cache.get(uuid);
        if (data == null) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                saveToFile(data);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void saveAll() {
        for (PlayerQuestData data : cache.values()) {
            saveToFile(data);
        }
    }

    private void saveToFile(PlayerQuestData data) {
        File file = fileFor(data.getUuid());
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, PlayerQuestEntry> entry : data.getEntries().entrySet()) {
            String questId = entry.getKey();
            PlayerQuestEntry e = entry.getValue();
            String path = "quests." + questId;

            config.set(path + ".status", e.getStatus().name());
            config.set(path + ".current_stage", e.getCurrentStage());
            config.set(path + ".completed_at", e.getCompletedAt());

            for (Map.Entry<String, Integer> obj : e.getObjectiveProgress().entrySet()) {
                config.set(path + ".objectives." + obj.getKey(), obj.getValue());
            }
        }

        try {
            config.save(file);
        } catch (IOException ex) {
            log.severe("[PlayerDataManager] Erreur sauvegarde " + data.getUuid() + " : " + ex.getMessage());
        }
    }

    private File fileFor(UUID uuid) {
        return new File(playersFolder, uuid.toString() + ".yml");
    }
}
