package fr.cozyhouse.cozyHouseQuests.model;

import fr.cozyhouse.cozyHouseQuests.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestReward {

    private RewardType reward;
    private String material;
    private int amount;
    private String command;

}
