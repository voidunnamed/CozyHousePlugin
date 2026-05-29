package fr.cozyhouse.cozyHouse.messages;

import fr.cozyhouse.cozyHouse.CozyHouseCore;
import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.gameplayer.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.cozyhouse.cozyHouse.utils.ConverterUtils.convertChatColor;
import static org.bukkit.Bukkit.getServer;

public class MessagesUtils {

    private static final MessageManager messageManager = CozyHouseCore.getInstance().getMessageManager();

    public static void sendConsoleMessage(ConsoleStateEnum state, String message) {
        getServer().getConsoleSender().sendMessage(state.prefix + message);
    }

    public static void broadCastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }

    public static void sendGamePlayerMessage(String message, GamePlayer gamePlayer) {
        String newMessage = convertChatColor(message);
        gamePlayer.getPlayer().sendMessage(newMessage);
    }

    public static void sendPlayerMessage(String message, Player player) {
        String newMessage = convertChatColor(message);
        player.sendMessage(newMessage);
    }

    public static List<String> getHelpCommand() {
        List<String> message = new ArrayList<>();
        message.add(convertChatColor(messageManager.getMessage(messageManager.getMessage(MessagesEnum.COMMANDS.HELP.HEADER.getPath()))));
        message.add(convertChatColor(messageManager.getMessage(MessagesEnum.COMMANDS.HELP.TITLE.getPath())));
        message.add(convertChatColor(messageManager.getMessage(MessagesEnum.COMMANDS.HELP.FOOTER.getPath())));

        List<String> helpMessages = messageManager.getMessageList(MessagesEnum.COMMANDS.HELP.LINES.getPath());
        for (String line : helpMessages) {
            message.add(convertChatColor(line));
        }
        return message;
    }

    public static void sendHelpMessage(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();
        for (String helplines : getHelpCommand()) {
            player.sendMessage(helplines);
        }
    }
}
