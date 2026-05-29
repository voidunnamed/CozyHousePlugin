package fr.cozyhouse.cozyHouse;

import fr.cozyhouse.cozyHouse.messages.ConsoleStateEnum;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import org.bukkit.ChatColor;

import static fr.cozyhouse.cozyHouse.messages.MessagesUtils.sendConsoleMessage;

public class ManagerRegistry {

    private final CozyHouseCore plugin;
    private final MessageManager messageManager;

    public ManagerRegistry(CozyHouseCore plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    public void loadAll() {
        for (ManagerType type : ManagerType.values()) {
            log(type, "LOADING");
            type.initialize(plugin);
            log(type, "LOADED");
        }
        logEnabledPlugin();
    }

    private void log(ManagerType type, String phase) {
        String message;
        switch (type) {
            case PLAYER_MANAGER:
                message = messageManager.getConsoleMessage(MessagesEnum.MANAGER.PLAYER_MANAGER.valueOf(phase).getPath());
                break;
            case EVENTS_MANAGER:
                message = messageManager.getConsoleMessage(MessagesEnum.MANAGER.EVENTS_MANAGER.valueOf(phase).getPath());
                break;
            case COMMANDS_MANAGER:
                message = messageManager.getConsoleMessage(MessagesEnum.MANAGER.COMMANDS_MANAGER.valueOf(phase).getPath());
                break;
            case TAB_ANIMATOR:
                message = messageManager.getConsoleMessage(MessagesEnum.MANAGER.TAB_ANIMATOR.valueOf(phase).getPath());
                break;
            default:
                throw new IllegalArgumentException("Unknown ManagerType: " + type);
        }
        if (message == null) {
            return;
        }
        sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', message));
    }

    private void logEnabledPlugin() {
        String message = messageManager.getConsoleMessage(MessagesEnum.GENERAL.PLUGIN_ENABLED.getPath());
        sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', message));
    }
}
