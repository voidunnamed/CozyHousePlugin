package fr.cozyhouse.cozyHouse.commands;

import fr.cozyhouse.cozyHouse.CozyHouseCore;
import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.gameplayer.GamePlayer;
import fr.cozyhouse.cozyHouse.gameplayer.PlayerManager;
import fr.cozyhouse.cozyHouse.messages.ConsoleStateEnum;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouse.messages.MessagesUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {

    private final CozyHouseCore plugin;

    private final MessageManager messageManager;
    private final PlayerManager playerManager;

    public AdminCommands() {
        this.plugin = CozyHouseCore.getInstance();
        this.messageManager = CozyHouseCore.getInstance().getMessageManager();
        this.playerManager = CozyHouseCore.getInstance().getPlayerManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player;
        GamePlayer gamePlayer;

        if (!(sender instanceof Player)) {
            MessagesUtils.sendConsoleMessage(ConsoleStateEnum.WARNING, messageManager.getConsoleMessage(MessagesEnum.COMMANDS.PLAYER_ONLY.getPath()));
            return true;
        } else {
            player = (Player) sender;
            gamePlayer = playerManager.getPlayer(player);

            if (gamePlayer == null) {
                MessagesUtils.sendPlayerMessage(messageManager.getMessage(MessagesEnum.PROGRAMERRORS.GAME_PLAYER_NOT_INITIALIZE.getPath()), player);
                return true;
            }
        }

        if (sender.hasPermission("admin")) {
            String newMoney;
            int newMoneyValue;
            switch (args.length) {
                case 0, 1:
                    MessagesUtils.sendGamePlayerMessage(messageManager.getMessage(MessagesEnum.COMMANDS.UNKNOWN_COMMAND.getPath()), gamePlayer);
                    return true;
                case 2:
                    switch (args[0]) {
                        case "addmoney":
                            newMoney = args[1];
                            newMoneyValue = Integer.parseInt(newMoney);
                            gamePlayer.addMoney(newMoneyValue);
                            MessagesUtils.sendGamePlayerMessage(messageManager.getMessage(MessagesEnum.COMMANDS.DISPLAY_MONEY.getPath(), "%number%", String.valueOf(gamePlayer.getMoney())), gamePlayer);
                            return true;
                        case "withdrawmoney":
                            newMoney = args[1];
                            newMoneyValue = Integer.parseInt(newMoney);
                            gamePlayer.withdrawMoney(newMoneyValue);
                            MessagesUtils.sendGamePlayerMessage(messageManager.getMessage(MessagesEnum.COMMANDS.DISPLAY_MONEY.getPath(), "%number%", String.valueOf(gamePlayer.getMoney())), gamePlayer);
                            return true;
                        case "setmoney":
                            newMoney = args[1];
                            newMoneyValue = Integer.parseInt(newMoney);
                            gamePlayer.setMoney(newMoneyValue);
                            MessagesUtils.sendGamePlayerMessage(messageManager.getMessage(MessagesEnum.COMMANDS.DISPLAY_MONEY.getPath(), "%number%", String.valueOf(gamePlayer.getMoney())), gamePlayer);
                            return true;
                        default:
                            MessagesUtils.sendGamePlayerMessage(messageManager.getMessage(MessagesEnum.COMMANDS.UNKNOWN_COMMAND.getPath()), gamePlayer);
                            MessagesUtils.sendHelpMessage(gamePlayer);
                            return true;
                    }
            }
        }
        return true;
    }
}
