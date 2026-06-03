package fr.cozyhouse.cozyHouseQuests.model;

import fr.cozyhouse.cozyHouseQuests.enums.QuestStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PlayerQuestEntry {

    private QuestStatus status;
    private String currentStage;

    // objectiveId → progression actuelle (ex: "chasse_obj_0" → 3 pour 3/5 loups)
    private final Map<String, Integer> objectiveProgress;

    // timestamp de completion (0 si pas encore terminée)
    private long completedAt;

    public PlayerQuestEntry(String startStage) {
        this.status = QuestStatus.IN_PROGRESS;
        this.currentStage = startStage;
        this.objectiveProgress = new HashMap<>();
        this.completedAt = 0;
    }

    public PlayerQuestEntry(QuestStatus status, String currentStage,
                            Map<String, Integer> objectiveProgress, long completedAt) {
        this.status = status;
        this.currentStage = currentStage;
        this.objectiveProgress = objectiveProgress;
        this.completedAt = completedAt;
    }

    public int getProgress(String objectiveId) {
        return objectiveProgress.getOrDefault(objectiveId, 0);
    }

    public void setProgress(String objectiveId, int value) {
        objectiveProgress.put(objectiveId, value);
    }

    public int incrementProgress(String objectiveId) {
        int newVal = getProgress(objectiveId) + 1;
        objectiveProgress.put(objectiveId, newVal);
        return newVal;
    }
}
