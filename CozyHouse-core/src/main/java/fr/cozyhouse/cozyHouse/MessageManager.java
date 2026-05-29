package fr.cozyhouse.cozyHouse;

import fr.cozyhouse.cozyHouse.messages.ConsoleStateEnum;
import fr.cozyhouse.cozyHouse.utils.ConverterUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static fr.cozyhouse.cozyHouse.messages.MessagesUtils.sendConsoleMessage;

public class MessageManager {

    private final CozyHouseCore plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public MessageManager() {
        this.plugin = CozyHouseCore.getInstance();
        diagnoseFileIssues();
        createMessagesFile();
        loadMessages();
    }

    private void diagnoseFileIssues() {
        sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&',
                "&e === DIAGNOSTIC MESSAGES.YML ==="));
        File pluginFolder = plugin.getDataFolder();
        sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&',
                "&6 Plugin folder : " + pluginFolder.getAbsolutePath()));
        boolean exists = pluginFolder.exists();
        String color = exists ? "&a" : "&c";
        ConsoleStateEnum state = exists ? ConsoleStateEnum.INFO : ConsoleStateEnum.WARNING;
        sendConsoleMessage(state, ChatColor.translateAlternateColorCodes('&',
                "&6 Folder exists : " + color + exists));

        File messagesFile = new File(pluginFolder, "stringMessages.yml");
        sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&',
                "&6 Messages file : " + messagesFile.getAbsolutePath()));
        exists = messagesFile.exists();
        color = exists ? "&a" : "&c";
        state = exists ? ConsoleStateEnum.INFO : ConsoleStateEnum.WARNING;
        sendConsoleMessage(state, ChatColor.translateAlternateColorCodes('&',
                "&6 File exists : " + color + exists));

        InputStream resourceStream = plugin.getResource("stringMessages.yml");
        exists = resourceStream != null;
        String message = exists ? "&aFOUND" : "&cNOT FOUND";
        state = exists ? ConsoleStateEnum.INFO : ConsoleStateEnum.WARNING;
        sendConsoleMessage(state, ChatColor.translateAlternateColorCodes('&',
                "&6 Ressource stringMessages.yml : " + (message)));

        if (resourceStream != null) {
            try {
                resourceStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', "&e === END DIAGNOSTIC ==="));
    }

    private void createMessagesFile() {
        messagesFile = new File(plugin.getDataFolder(), "stringMessages.yml");

        if (!plugin.getDataFolder().exists()) {
            boolean created = plugin.getDataFolder().mkdirs();

            ConsoleStateEnum state = created ? ConsoleStateEnum.INFO : ConsoleStateEnum.WARNING;
            sendConsoleMessage(state, ChatColor.translateAlternateColorCodes('&', "&3 Folder creation : " + (created ? "&aSUCCES" : "&cFAILURE")));
        }

        if (!messagesFile.exists()) {
            sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', "&5 Creation of file stringMessages.yml..."));

            try {
                if (plugin.getResource("stringMessages.yml") != null) {
                    plugin.saveResource("stringMessages.yml", false);
                    sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', "&d stringMessages.yml create from ressources !"));
                } else {
                    boolean created = messagesFile.createNewFile();
                    sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', "&2 Void file stringMessages.yml create : " + created));
                }
            } catch (Exception e) {
                sendConsoleMessage(ConsoleStateEnum.SEVERE, ChatColor.translateAlternateColorCodes('&', "&c Error creation stringMessages.yml : " + e.getMessage()));
            }
        } else {
            sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', "&1 stringMessages.yml already exist"));
        }

        boolean exists = messagesFile.exists();
        String color = exists ? "&a" : "&c";
        ConsoleStateEnum state = exists ? ConsoleStateEnum.INFO : ConsoleStateEnum.WARNING;
        sendConsoleMessage(state, ChatColor.translateAlternateColorCodes('&', "&2 Final state - File exists : " + color + exists));
        if (messagesFile.exists()) {
            sendConsoleMessage(ConsoleStateEnum.INFO, ChatColor.translateAlternateColorCodes('&', "&6 File size : &e" + messagesFile.length() + " octets"));
        }
    }

    public void loadMessages() {
        if (messagesFile.exists()) {
            try {
                String rawContent = Files.readString(messagesFile.toPath());
                messagesConfig = new YamlConfiguration();
                messagesConfig.loadFromString(rawContent);
                plugin.getLogger().info(" Messages chargés : " + messagesConfig.getKeys(false).size() + " sections");
            } catch (Exception e) {
                plugin.getLogger().severe(" Erreur lors du chargement des messages : " + e.getMessage());
                messagesConfig = new YamlConfiguration();
            }
        } else {
            plugin.getLogger().severe(" Impossible de charger stringMessages.yml - fichier non trouvé !");
            messagesConfig = new YamlConfiguration();
        }
    }

    public void reloadMessages() {
        loadMessages();
        plugin.getLogger().info(" Messages rechargés");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  GET MESSAGE (avec prefix)
    // ─────────────────────────────────────────────────────────────────────────────

    public String getMessage(String path) {
        if (messagesConfig == null) return "&cMessageManager not initialized!";
        String prefix = messagesConfig.getString("general.prefix");
        String message = messagesConfig.getString(path);
        prefix = prefix != null ? prefix : "";
        message = message != null ? message : "&cMessage not found: " + path;
        return ConverterUtils.convertChatColor(prefix + message);
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        if (message == null) return "&cMessage not found: " + path;
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String search = replacements[i] != null ? replacements[i] : "";
                String replacement = replacements[i + 1] != null ? replacements[i + 1] : "";
                message = message.replace(search, replacement);
            }
        }
        return message;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  GET MESSAGE (sans prefix)
    // ─────────────────────────────────────────────────────────────────────────────

    public String getMessageNoPrefix(String path) {
        if (messagesConfig == null) return "&cMessageManager not initialized!";
        String message = messagesConfig.getString(path);
        message = message != null ? message : "&cMessage not found: " + path;
        return ConverterUtils.convertChatColor(message);
    }

    public String getMessageNoPrefix(String path, String... replacements) {
        String message = getMessageNoPrefix(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String search = replacements[i] != null ? replacements[i] : "";
                String replacement = replacements[i + 1] != null ? replacements[i + 1] : "";
                message = message.replace(search, replacement);
            }
        }
        return message;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  CONSOLE
    // ─────────────────────────────────────────────────────────────────────────────

    public String getConsoleMessage(String path) {
        if (messagesConfig == null) return "&cMessageManager not initialized!";
        return ChatColor.translateAlternateColorCodes('&',
                messagesConfig.getString("general.prefix") + messagesConfig.getString(path));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  GET LIST
    // ─────────────────────────────────────────────────────────────────────────────

    public List<String> getMessageList(String path) {
        if (messagesConfig == null) return List.of("&cMessageManager non initialisé !");
        List<String> messages = messagesConfig.getStringList(path);
        messages.replaceAll(msg -> ChatColor.translateAlternateColorCodes('&', msg));
        return messages;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  SEND TO PLAYER (avec prefix)
    // ─────────────────────────────────────────────────────────────────────────────

    public void send(Player player, String path) {
        player.sendMessage(getMessage(path));
    }

    public void send(Player player, String path, String... replacements) {
        player.sendMessage(getMessage(path, replacements));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  SEND TO PLAYER (sans prefix)
    // ─────────────────────────────────────────────────────────────────────────────

    public void sendNoPrefix(Player player, String path) {
        player.sendMessage(getMessageNoPrefix(path));
    }

    public void sendNoPrefix(Player player, String path, String... replacements) {
        player.sendMessage(getMessageNoPrefix(path, replacements));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  SEND LIST
    // ─────────────────────────────────────────────────────────────────────────────

    public void sendList(Player player, String path) {
        getMessageList(path).forEach(player::sendMessage);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  BROADCAST
    // ─────────────────────────────────────────────────────────────────────────────

    public void broadcast(String path) {
        plugin.getServer().broadcastMessage(getMessage(path));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  SAVE
    // ─────────────────────────────────────────────────────────────────────────────

    public void saveMessages() {
        if (messagesConfig != null && messagesFile != null) {
            try {
                messagesConfig.save(messagesFile);
                plugin.getLogger().info(" stringMessages.yml sauvegardé");
            } catch (IOException e) {
                plugin.getLogger().severe(" Erreur sauvegarde stringMessages.yml: " + e.getMessage());
            }
        }
    }
}
