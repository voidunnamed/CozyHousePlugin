package fr.cozyhouse.cozyHouseRPG.skills.datas;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TessereSlot {

    HEAD      ("Tête",         13, "Mental"),
    CHEST     ("Torse",        22, "Vital"),
    RIGHT_ARM ("Bras Droit",   20, "Offensif"),
    LEFT_ARM  ("Bras Gauche",  24, "Secondaire"),
    RIGHT_LEG ("Jambe Droite", 29, "Mobilité"),
    LEFT_LEG  ("Jambe Gauche", 33, "Ancrage");

    private final String displayName;
    private final int guiSlotIndex;
    private final String nodeLabel;
}
