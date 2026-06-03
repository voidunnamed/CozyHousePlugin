package fr.cozyhouse.cozyHouseQuests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Quest {
    private String id;
    private String name;
    private String description;
    private String startStage;
    private boolean repeatable;
    private int cooldownSeconds;
    private Map<String, QuestStage> stages;

}
