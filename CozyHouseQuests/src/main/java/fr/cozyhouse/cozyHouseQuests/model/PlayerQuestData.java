package fr.cozyhouse.cozyHouseQuests.model;

import fr.cozyhouse.cozyHouseQuests.enums.QuestStatus;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerQuestData {

    @Getter
    private final UUID uuid;

    // questId → état complet de cette quête pour ce joueur
    @Getter
    private final Map<String, PlayerQuestEntry> entries;

    public PlayerQuestData(UUID uuid) {
        this.uuid = uuid;
        this.entries = new HashMap<>();
    }

    public PlayerQuestEntry getEntry(String questId) {
        return entries.get(questId);
    }

    public boolean hasEntry(String questId) {
        return entries.containsKey(questId);
    }

    public void putEntry(String questId, PlayerQuestEntry entry) {
        entries.put(questId, entry);
    }

    public QuestStatus getStatus(String questId) {
        PlayerQuestEntry entry = entries.get(questId);
        return entry == null ? QuestStatus.NOT_STARTED : entry.getStatus();
    }
}
