package fr.cozyhouse.cozyHouseRPG.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completer pour /chrpg.
 *
 */
public class RaceTabCompleter implements TabCompleter {

    private static final List<String> SUB_COMMANDS_USER  = Arrays.asList("info", "stats", "tesseres");
    private static final List<String> SUB_COMMANDS_ADMIN = Arrays.asList("info", "stats", "changerace", "exp", "level", "tesseres", "wand");
    private static final List<String> TESSERES_ACTIONS   = Arrays.asList("info", "reset", "discover");
    private static final List<String> TESSERE_NAMES;
    static {
        List<String> names = new ArrayList<>();
        names.add("all");
        for (fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType t
                : fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType.values()) {
            names.add(t.name().toLowerCase());
        }
        TESSERE_NAMES = Collections.unmodifiableList(names);
    }
    private static final List<String> ACTION_COMMANDS    = Arrays.asList("give", "set");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player player)) return completions;

        boolean isAdmin = player.isOp() || player.hasPermission(RaceCommand.PERM_ADMIN);
        boolean isUser  = player.hasPermission(RaceCommand.PERM_USER);

        if (!isUser && !isAdmin) return completions;

        switch (args.length) {

            case 1 -> {
                // Sous-commandes disponibles selon la permission
                List<String> available = isAdmin ? SUB_COMMANDS_ADMIN : SUB_COMMANDS_USER;
                completions = filter(available, args[0]);
            }

            case 2 -> {
                switch (args[0].toLowerCase()) {
                    // /chrpg stats <joueur>   → admin seulement
                    case "stats" -> {
                        if (isAdmin) completions = onlinePlayers(args[1]);
                    }
                    // /chrpg exp <give|set>
                    case "exp" -> {
                        if (isAdmin) completions = filter(ACTION_COMMANDS, args[1]);
                    }
                    // /chrpg level <give|set>
                    case "level" -> {
                        if (isAdmin) completions = filter(ACTION_COMMANDS, args[1]);
                    }
                    // /chrpg tesseres <info|reset|discover>
                    case "tesseres" -> {
                        List<String> available = isAdmin ? TESSERES_ACTIONS : List.of("info");
                        completions = filter(available, args[1]);
                    }
                }
            }

            case 3 -> {
                if (isAdmin) {
                    String sub = args[0].toLowerCase();
                    // /chrpg exp <give|set> <player>
                    // /chrpg level <give|set> <player>
                    if ((sub.equals("exp") || sub.equals("level"))
                            && ACTION_COMMANDS.contains(args[1].toLowerCase())) {
                        completions = onlinePlayers(args[2]);
                    }
                    // /chrpg tesseres discover <tessere|all>
                    if (sub.equals("tesseres") && args[1].equalsIgnoreCase("discover")) {
                        completions = filter(TESSERE_NAMES, args[2]);
                    }
                }
            }

            case 4 -> {
                // /chrpg exp <give|set> <joueur> <montant>
                // /chrpg level <give|set> <joueur> <montant>
                if (isAdmin) {
                    String sub = args[0].toLowerCase();
                    if (sub.equals("exp") || sub.equals("level")) {
                        completions = List.of("<montant>");
                    }
                }
            }
        }

        return completions;
    }

    // ─────────────────────────────────────────────────────────────────────────────

    /** Filtre une liste selon ce que le joueur a déjà tapé. */
    private List<String> filter(List<String> list, String typed) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(typed.toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Retourne les noms des joueurs en ligne correspondant au préfixe tapé. */
    private List<String> onlinePlayers(String typed) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(typed.toLowerCase()))
                .collect(Collectors.toList());
    }
}
