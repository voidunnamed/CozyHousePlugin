package fr.cozyhouse.cozyHouseQuests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestStage {

    private String id;
    private String dialogue;
    private List<QuestObjective> objectives;
    private List<QuestChoice> choices;
    private String nextStage;
    private List<QuestReward> rewards;

}
