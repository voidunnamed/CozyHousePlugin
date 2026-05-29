package fr.cozyhouse.cozyHouse.gameplayer;

import fr.cozyhouse.cozyHouse.CozyHouseCore;
import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.ConsoleStateEnum;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouse.messages.MessagesUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
@Setter
public class GamePlayer {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private CozyHouseCore plugin;

    private MessageManager messageManager;

    private UUID uuid;
    private Player player;
    private String name;

    private int money = 0;
    private NamespacedKey keyMoney;
    private PersistentDataContainer pdc;

    public GamePlayer(UUID uuid) {
        this.plugin = CozyHouseCore.getInstance();
        this.messageManager = CozyHouseCore.getInstance().getMessageManager();
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.name = Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName();
        this.keyMoney = new NamespacedKey(plugin, "money");
        this.pdc = Objects.requireNonNull(Bukkit.getPlayer(uuid)).getPersistentDataContainer();
    }

    public void initializeMoney() {
        if (this.pdc.get(this.keyMoney, PersistentDataType.INTEGER) != null) {
            this.money = pdc.get(this.keyMoney, PersistentDataType.INTEGER);
        } else {
            this.pdc.set(this.keyMoney, PersistentDataType.INTEGER, 0);
            this.money = this.pdc.getOrDefault(this.keyMoney, PersistentDataType.INTEGER, 0);
        }
    }

    public void addMoney(int number) {
        if (this.pdc.get(this.keyMoney, PersistentDataType.INTEGER) == null) {
            MessagesUtils.sendConsoleMessage(ConsoleStateEnum.WARNING, messageManager.getConsoleMessage(MessagesEnum.PROGRAMERRORS.PERSISTANT_DATA_MISSING.getPath()));
            return;
        }

        this.money = money + number;
        int newValue = this.pdc.get(this.keyMoney, PersistentDataType.INTEGER) + number;
        this.pdc.set(this.keyMoney, PersistentDataType.INTEGER, newValue);
    }

    public void withdrawMoney(int number) {
        if (this.pdc.get(this.keyMoney, PersistentDataType.INTEGER) == null) {
            MessagesUtils.sendConsoleMessage(ConsoleStateEnum.WARNING, messageManager.getConsoleMessage(MessagesEnum.PROGRAMERRORS.PERSISTANT_DATA_MISSING.getPath()));
            return;
        }

        if ((this.money - number) <= 0) {
            this.money = 0;
            this.pdc.set(this.keyMoney, PersistentDataType.INTEGER, 0);
        } else {
            this.money = this.money - number;
            int newValue = this.pdc.get(this.keyMoney, PersistentDataType.INTEGER) - number;
            this.pdc.set(this.keyMoney, PersistentDataType.INTEGER, newValue);
        }
    }

    public void setMoney(int newValue) {
        if (this.pdc.get(this.keyMoney, PersistentDataType.INTEGER) == null) {
            MessagesUtils.sendConsoleMessage(ConsoleStateEnum.WARNING, messageManager.getConsoleMessage(MessagesEnum.PROGRAMERRORS.PERSISTANT_DATA_MISSING.getPath()));
            return;
        }

        this.money = newValue;
        this.pdc.set(this.keyMoney, PersistentDataType.INTEGER, newValue);
    }
}
