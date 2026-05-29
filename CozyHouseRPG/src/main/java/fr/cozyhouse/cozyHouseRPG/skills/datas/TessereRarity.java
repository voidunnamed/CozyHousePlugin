package fr.cozyhouse.cozyHouseRPG.skills.datas;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TessereRarity {

    BRUT     ("&f&lFragment Brut",      1),
    TAILLE   ("&b&lFragment Taillé",    12),
    FACETTE  ("&d&lFragment Facetté",   25),
    ECLAT    ("&6&lÉclat Arcanique",    40);

    private final String displayName;
    private final int requiredLevel;
}
