package fr.cozyhouse.cozyHouseRPG.commands;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.gui.RaceSelectionGUI;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import fr.cozyhouse.cozyHouseRPG.skills.gui.TessereBodyGUI;
import fr.cozyhouse.cozyHouseRPG.skills.items.ArcaneWand;
import fr.cozyhouse.cozyHouseRPG.skills.playerstate.TessereLoadout;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.player.experience.ExperienceService;
import fr.cozyhouse.cozyHouseRPG.race.Race;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Gère toutes les commandes liées au RPG (/chrpg).
 *
 * Permissions :
 *   cozyhouserpg.user  → info, stats (ses propres stats)
 *   cozyhouserpg.admin → changerace, stats <joueur>, exp give/set, level give/set
 */
@RequiredArgsConstructor
public class RaceCommand implements CommandExecutor {

    static final String PERM_USER  = "cozyhouserpg.user";
    static final String PERM_ADMIN = "cozyhouserpg.admin";

    private final PluginContext context;

    // ─────────────────────────────────────────────────────────────────────────────
    // Entrée principale
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageManager msg = context.getMessageManager();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.PLAYER_ONLY.getPath()));
            return true;
        }

        if (!player.isOp() && !player.hasPermission(PERM_USER) && !player.hasPermission(PERM_ADMIN)) {
            msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
            return true;
        }

        if (args.length == 0) {
            sendHelp(player, msg);
            return true;
        }

        RPGPlayer rpgPlayer = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpgPlayer == null) {
            msg.send(player, MessagesEnum.PROGRAMERRORS.GAME_PLAYER_NOT_INITIALIZE.getPath());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"       -> handleInfo(player, rpgPlayer, msg);
            case "stats"      -> handleStats(player, rpgPlayer, args, msg);
            case "changerace" -> handleChangeRace(player, msg);
            case "exp"        -> handleExp(player, args, msg);
            case "level"      -> handleLevel(player, args, msg);
            case "tesseres"   -> handleTesseres(player, rpgPlayer, args, msg);
            case "wand"       -> handleWand(player, msg);
            default           -> sendHelp(player, msg);
        }

        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // /chrpg info   [perm: user]
    // ─────────────────────────────────────────────────────────────────────────────

    private void handleInfo(Player player, RPGPlayer rpgPlayer, MessageManager msg) {
        if (!hasUserOrAdmin(player)) {
            msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
            return;
        }
        showRaceInfo(player, rpgPlayer, msg);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // /chrpg stats [joueur]   [perm: user pour soi / admin pour autrui]
    // ─────────────────────────────────────────────────────────────────────────────

    private void handleStats(Player player, RPGPlayer selfRpg, String[] args, MessageManager msg) {
        if (args.length >= 2) {
            if (!hasAdmin(player)) {
                msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
                return;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG.PLAYER_NOT_FOUND.getPath(),
                        "%player%", args[1]);
                return;
            }
            RPGPlayer targetRpg = context.getPlayerService().getPlayer(target).orElse(null);
            if (targetRpg == null) {
                msg.send(player, MessagesEnum.PROGRAMERRORS.GAME_PLAYER_NOT_INITIALIZE.getPath());
                return;
            }
            showStats(player, targetRpg, target.getName(), msg);
        } else {
            if (!hasUserOrAdmin(player)) {
                msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
                return;
            }
            showStats(player, selfRpg, player.getName(), msg);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // /chrpg tesseres [reset]   [perm: user / admin pour reset]
    // ─────────────────────────────────────────────────────────────────────────────

    private void handleTesseres(Player player, RPGPlayer rpgPlayer, String[] args, MessageManager msg) {
        if (!hasUserOrAdmin(player)) {
            msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
            return;
        }

        // /chrpg tesseres discover <tessere|all>  →  admin only, grants discovery
        if (args.length >= 2 && args[1].equalsIgnoreCase("discover")) {
            if (!hasAdmin(player)) {
                msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
                return;
            }
            if (args.length < 3) {
                player.sendMessage("&cUsage: &e/chrpg tesseres discover <tessere|all>");
                return;
            }
            String target = args[2];
            if (target.equalsIgnoreCase("all")) {
                rpgPlayer.getTessereLoadout().discoverAll();
                player.sendMessage(msg.getMessageNoPrefix(
                        MessagesEnum.TESSERE.COMMANDS.DISCOVER_ALL_SUCCESS.getPath()));
            } else {
                try {
                    fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType type =
                            fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType.valueOf(target.toUpperCase());
                    rpgPlayer.getTessereLoadout().discover(type);
                    player.sendMessage(msg.getMessageNoPrefix(
                            MessagesEnum.TESSERE.COMMANDS.DISCOVER_SUCCESS.getPath(),
                            "%tessere%", type.getDisplayName()));
                } catch (IllegalArgumentException e) {
                    player.sendMessage("&cUnknown Tessere: &e" + target
                            + "&c. Use &e/chrpg tesseres discover all &cto discover everything.");
                }
            }
            return;
        }

        // /chrpg tesseres reset  →  admin only, clears the loadout for testing
        if (args.length >= 2 && args[1].equalsIgnoreCase("reset")) {
            if (!hasAdmin(player)) {
                msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
                return;
            }
            TessereLoadout loadout = rpgPlayer.getTessereLoadout();
            for (TessereSlot slot : TessereSlot.values()) {
                loadout.unequipFragment(slot);
            }
            context.getPlayerService().applyRaceStats(rpgPlayer);
            player.sendMessage(msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMMANDS.RESET_SUCCESS.getPath()));
            return;
        }

        // /chrpg tesseres info  →  shows loadout state in chat
        if (args.length >= 2 && args[1].equalsIgnoreCase("info")) {
            TessereLoadout loadout = rpgPlayer.getTessereLoadout();
            String sep = msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMMANDS.INFO_SEPARATOR.getPath());
            player.sendMessage(sep);
            player.sendMessage(msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMMANDS.INFO_TITLE.getPath()));
            for (TessereSlot slot : TessereSlot.values()) {
                Tessere t = loadout.getEquippedFragments().get(slot);
                String tessereNom = (t != null)
                        ? "\u00a7f" + t.getDisplayName()
                        : msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMMANDS.INFO_SLOT_EMPTY.getPath());
                player.sendMessage(msg.getMessageNoPrefix(
                        MessagesEnum.TESSERE.COMMANDS.INFO_SLOT_LINE.getPath(),
                        "%slot%",    slot.getDisplayName(),
                        "%tessere%", tessereNom));
            }
            if (loadout.isRevealed()) {
                player.sendMessage("");
                player.sendMessage(msg.getMessageNoPrefix(
                        MessagesEnum.TESSERE.COMMANDS.INFO_SCORE.getPath(),
                        "%value%", String.format("%.0f%%", loadout.getEfficaciteScore() * 100)));
                loadout.getActiveSkills().forEach(s -> player.sendMessage(
                        msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMMANDS.INFO_ABILITY.getPath(),
                                "%nom%",         s.getDisplayName(),
                                "%description%", s.getDescription())));
            } else {
                player.sendMessage(msg.getMessageNoPrefix(MessagesEnum.TESSERE.COMMANDS.INFO_NOT_REVEALED.getPath()));
            }
            player.sendMessage(sep);
            return;
        }

        // /chrpg tesseres   → ouvre le GUI
        new TessereBodyGUI(context).ouvrir(rpgPlayer);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // /chrpg wand   [perm: admin] — gives the Arcane Wand
    // ─────────────────────────────────────────────────────────────────────────────

    private void handleWand(Player player, MessageManager msg) {
        if (!hasAdmin(player)) {
            msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
            return;
        }
        player.getInventory().addItem(ArcaneWand.build());
        player.sendMessage("&a✦ Arcane Wand given.");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // /chrpg changerace   [perm: admin]
    // ─────────────────────────────────────────────────────────────────────────────

    private void handleChangeRace(Player player, MessageManager msg) {
        if (!hasAdmin(player)) {
            msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
            return;
        }
        new RaceSelectionGUI(context).open(player);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // /chrpg exp <give|set> <joueur> <montant>   [perm: admin]
    // ─────────────────────────────────────────────────────────────────────────────

    private void handleExp(Player player, String[] args, MessageManager msg) {
        if (!hasAdmin(player)) {
            msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
            return;
        }

        if (args.length < 4) {
            msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_EXP.USAGE.getPath());
            return;
        }

        String action     = args[1].toLowerCase();
        String targetName = args[2];
        long amount;

        try {
            amount = Long.parseLong(args[3]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG.INVALID_AMOUNT.getPath());
            return;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG.PLAYER_NOT_FOUND.getPath(),
                    "%player%", targetName);
            return;
        }

        RPGPlayer targetRpg = context.getPlayerService().getPlayer(target).orElse(null);
        if (targetRpg == null) {
            msg.send(player, MessagesEnum.PROGRAMERRORS.GAME_PLAYER_NOT_INITIALIZE.getPath());
            return;
        }

        ExperienceService expSvc = targetRpg.getExperienceService();

        switch (action) {
            case "give" -> {
                if (expSvc.isMaxLevel()) {
                    msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_EXP.ALREADY_MAX.getPath(),
                            "%player%", target.getName());
                    return;
                }
                long added = expSvc.addExperience(amount);
                msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_EXP.GIVE_SUCCESS_ADMIN.getPath(),
                        "%amount%", String.valueOf(added),
                        "%player%", target.getName(),
                        "%xp%", expSvc.getXpDisplay());
                msg.sendNoPrefix(target, MessagesEnum.COMMANDS.RPG_EXP.GIVE_SUCCESS_PLAYER.getPath(),
                        "%amount%", String.valueOf(added));
            }
            case "set" -> {
                expSvc.setExperienceAndCheck(amount);
                msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_EXP.SET_SUCCESS_ADMIN.getPath(),
                        "%player%", target.getName(),
                        "%level%", expSvc.getLevelDisplay(),
                        "%xp%", expSvc.getXpDisplay());
                msg.sendNoPrefix(target, MessagesEnum.COMMANDS.RPG_EXP.SET_SUCCESS_PLAYER.getPath(),
                        "%level%", expSvc.getLevelDisplay());
            }
            default -> msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG.UNKNOWN_ACTION.getPath());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // /chrpg level <give|set> <joueur> <montant>   [perm: admin]
    // ─────────────────────────────────────────────────────────────────────────────

    private void handleLevel(Player player, String[] args, MessageManager msg) {
        if (!hasAdmin(player)) {
            msg.send(player, MessagesEnum.COMMANDS.NO_PERMISSION.getPath());
            return;
        }

        if (args.length < 4) {
            msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_LEVEL.USAGE.getPath());
            return;
        }

        String action     = args[1].toLowerCase();
        String targetName = args[2];
        int amount;

        try {
            amount = Integer.parseInt(args[3]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG.INVALID_AMOUNT.getPath());
            return;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG.PLAYER_NOT_FOUND.getPath(),
                    "%player%", targetName);
            return;
        }

        RPGPlayer targetRpg = context.getPlayerService().getPlayer(target).orElse(null);
        if (targetRpg == null) {
            msg.send(player, MessagesEnum.PROGRAMERRORS.GAME_PLAYER_NOT_INITIALIZE.getPath());
            return;
        }

        ExperienceService expSvc = targetRpg.getExperienceService();
        int maxLevel = (int) expSvc.getMaxLevel();

        switch (action) {
            case "give" -> {
                if (expSvc.isMaxLevel()) {
                    msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_EXP.ALREADY_MAX.getPath(),
                            "%player%", target.getName());
                    return;
                }
                int newLevel = (int) Math.min(targetRpg.getLevel() + amount, maxLevel);
                int actualGiven = newLevel - (int) targetRpg.getLevel();

                if (actualGiven < amount) {
                    msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_LEVEL.CAPPED.getPath(),
                            "%max%", String.valueOf(maxLevel));
                }

                targetRpg.setLevel(newLevel);
                targetRpg.setExperience(0);
                context.getPlayerService().refreshAttributes(targetRpg);

                msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_LEVEL.GIVE_SUCCESS_ADMIN.getPath(),
                        "%amount%", String.valueOf(actualGiven),
                        "%player%", target.getName(),
                        "%level%", expSvc.getLevelDisplay());
                msg.sendNoPrefix(target, MessagesEnum.COMMANDS.RPG_LEVEL.GIVE_SUCCESS_PLAYER.getPath(),
                        "%amount%", String.valueOf(actualGiven),
                        "%level%", expSvc.getLevelDisplay());
            }
            case "set" -> {
                int capped = Math.min(amount, maxLevel);

                if (capped != amount) {
                    msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_LEVEL.CAPPED.getPath(),
                            "%max%", String.valueOf(maxLevel));
                }

                targetRpg.setLevel(capped);
                targetRpg.setExperience(0);
                context.getPlayerService().refreshAttributes(targetRpg);

                msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_LEVEL.SET_SUCCESS_ADMIN.getPath(),
                        "%player%", target.getName(),
                        "%level%", expSvc.getLevelDisplay());
                msg.sendNoPrefix(target, MessagesEnum.COMMANDS.RPG_LEVEL.SET_SUCCESS_PLAYER.getPath(),
                        "%level%", expSvc.getLevelDisplay());
            }
            default -> msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG.UNKNOWN_ACTION.getPath());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Affichage des stats complètes
    // ─────────────────────────────────────────────────────────────────────────────

    private void showStats(Player viewer, RPGPlayer rpg, String playerName, MessageManager msg) {
        String raceName = rpg.hasRace()
                ? context.getRaceRegistry().getRace(rpg.getCurrentRace()).getDisplayName()
                : msg.getMessageNoPrefix(MessagesEnum.PROGRAMERRORS.RACE_NOT_SELECTED.getPath());

        ExperienceService expSvc = rpg.getExperienceService();

        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.HEADER.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.TITLE.getPath(),
                "%player%", playerName));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.HEADER.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.RACE_LEVEL_XP.getPath(),
                "%race%", raceName,
                "%level%", expSvc.getLevelDisplay(),
                "%xp%", expSvc.getXpDisplay()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.SKILL_POINTS.getPath(),
                "%points%", String.valueOf(rpg.getSkillPoints())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.EMPTY_LINE.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.HEALTH.getPath(),
                "%health%", String.format("%.1f", rpg.getHealth()),
                "%maxhealth%", String.format("%.1f", rpg.getMaxHealth())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.EMPTY_LINE.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.ATTRIBUTES_HEADER.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.STRENGTH.getPath(),
                "%value%", String.format("%.0f", rpg.getStrength())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.DEXTERITY.getPath(),
                "%value%", String.format("%.0f", rpg.getDexterity())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.CONSTITUTION.getPath(),
                "%value%", String.format("%.0f", rpg.getConstitution())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.INTELLIGENCE.getPath(),
                "%value%", String.format("%.0f", rpg.getIntelligence())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.CHARISMA.getPath(),
                "%value%", String.format("%.0f", rpg.getCharisma())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.EMPTY_LINE.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.COMBAT_HEADER.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.ATTACK_DAMAGE.getPath(),
                "%value%", String.format("%.2f", rpg.getAttackDamage())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.ATTACK_SPEED.getPath(),
                "%value%", String.format("%.2f", rpg.getAttackSpeed())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.ARMOR.getPath(),
                "%value%", String.format("%.1f", rpg.getArmor())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.TOUGHNESS.getPath(),
                "%value%", String.format("%.1f", rpg.getToughness())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.CRIT_CHANCE.getPath(),
                "%value%", String.format("%.1f%%", rpg.getCritChance() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.CRIT_DAMAGE.getPath(),
                "%value%", String.format("%.1fx", rpg.getCritDamage())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.DODGE.getPath(),
                "%value%", String.format("%.1f%%", rpg.getDodgeChance() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.KB_RESISTANCE.getPath(),
                "%value%", String.format("%.0f%%", rpg.getKnockbackResistance() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.EMPTY_LINE.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.BONUSES_HEADER.getPath()));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.EXP_BONUS.getPath(),
                "%value%", String.format("+%.0f%%", rpg.getExpBonus() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.LOOT_BONUS.getPath(),
                "%value%", String.format("+%.0f%%", rpg.getLootBonus() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.TRADE_DISCOUNT.getPath(),
                "%value%", String.format("-%.0f%%", rpg.getTradeDiscount() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.POISON_RESIST.getPath(),
                "%value%", String.format("%.0f%%", rpg.getPoisonResistance() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.FALL_REDUCE.getPath(),
                "%value%", String.format("%.0f%%", rpg.getFallDamageReduction() * 100)));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.SWIM_SPEED.getPath(),
                "%value%", String.format("%.1fx", rpg.getSwimSpeedMultiplier())));
        viewer.sendMessage(msg.getMessageNoPrefix(MessagesEnum.COMMANDS.RPG_STATS.FOOTER.getPath()));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Affichage info race
    // ─────────────────────────────────────────────────────────────────────────────

    private void showRaceInfo(Player player, RPGPlayer rpgPlayer, MessageManager msg) {
        if (!rpgPlayer.hasRace()) {
            msg.send(player, MessagesEnum.RACE.SELECTION.NO_RACE.getPath());
            msg.send(player, MessagesEnum.RACE.SELECTION.USE_COMMAND.getPath());
            return;
        }

        Race race = context.getRaceRegistry().getRace(rpgPlayer.getCurrentRace());

        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.HEADER.getPath());
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.YOUR_RACE.getPath(),
                "%color%", race.getChatColor().toString(),
                "%race%", race.getDisplayName());
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.DESCRIPTION_LABEL.getPath(),
                "%description%", race.getDescription());
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.EMPTY_LINE.getPath());
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.STATS_HEADER.getPath());
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.LEVEL_XP.getPath(),
                "%level%", rpgPlayer.getExperienceService().getLevelDisplay(),
                "%xp%",   rpgPlayer.getExperienceService().getXpDisplay());
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.HEALTH.getPath(),
                "%health%", String.format("%.1f", rpgPlayer.getHealth()),
                "%maxhealth%", String.format("%.1f", rpgPlayer.getMaxHealth()));
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.STATS_LINE.getPath(),
                "%strength%", String.format("%.0f", rpgPlayer.getStrength()),
                "%dexterity%", String.format("%.0f", rpgPlayer.getDexterity()));
        msg.sendNoPrefix(player, MessagesEnum.RACE.INFO.FOOTER.getPath());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Aide
    // ─────────────────────────────────────────────────────────────────────────────

    private void sendHelp(Player player, MessageManager msg) {
        msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_HELP.HEADER.getPath());
        msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_HELP.TITLE.getPath());
        msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_HELP.EMPTY_LINE.getPath());
        msg.sendList(player, MessagesEnum.COMMANDS.RPG_HELP.LINES.getPath());
        msg.sendNoPrefix(player, MessagesEnum.COMMANDS.RPG_HELP.FOOTER.getPath());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helpers permissions
    // ─────────────────────────────────────────────────────────────────────────────

    private boolean hasUserOrAdmin(Player player) {
        return player.isOp()
                || player.hasPermission(PERM_USER)
                || player.hasPermission(PERM_ADMIN);
    }

    private boolean hasAdmin(Player player) {
        return player.isOp() || player.hasPermission(PERM_ADMIN);
    }
}
