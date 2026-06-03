package fr.cozyhouse.cozyHouseQuests.model;

import fr.cozyhouse.cozyHouseQuests.enums.ObjectiveType;
import fr.cozyhouse.cozyHouseQuests.enums.QuestCondition;
import fr.cozyhouse.cozyHouseQuests.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestObjective {

    private String id;
    private ObjectiveType type;
    private TargetType target;
    private int amount;
    private String label;
    private List<QuestCondition> conditions;

}
