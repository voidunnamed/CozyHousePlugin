package fr.cozyhouse.cozyHouse.api;

import fr.cozyhouse.cozyHouse.CozyHouseCore;
import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.gameplayer.PlayerManager;

public class CozyHouseAPI {

    private static CozyHouseCore getCozyHouse() {
        return CozyHouseCore.getInstance();   // ← tu peux le garder ici, car cette classe est dans CozyHouseCore.jar
    }

    public static PlayerManager getPlayerManager() {
        CozyHouseCore cozy = getCozyHouse();
        if (cozy == null) {
            throw new IllegalStateException("CozyHouseCore n'est pas initialisé !");
        }
        return cozy.getPlayerManager();
    }

    public static MessageManager getMessageManager() {
        CozyHouseCore cozy = getCozyHouse();
        if (cozy == null) {
            throw new IllegalStateException("CozyHouseCore n'est pas initialisé !");
        }
        return cozy.getMessageManager();
    }
}


