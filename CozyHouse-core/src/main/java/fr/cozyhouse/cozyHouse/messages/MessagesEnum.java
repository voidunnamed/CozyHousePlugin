package fr.cozyhouse.cozyHouse.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;


public enum MessagesEnum {
    ;

    @AllArgsConstructor
    @Getter
    public enum GENERAL {
        ORIGINAL("general."),
        PREFIX(ORIGINAL.path + "prefix"),
        PLUGIN_ENABLED(ORIGINAL.path + "plugin-enabled"),
        PLUGIN_DISABLED(ORIGINAL.path + "plugin-disabled");

        private final String path;
    }

    @AllArgsConstructor
    @Getter
    public enum MANAGER {
        ORIGINAL("manager.");

        @AllArgsConstructor
        @Getter
        public enum MESSAGE_MANAGER {
            ORIGINAL(MANAGER.ORIGINAL.path + "messageManager."),
            LOADING(ORIGINAL.path + "loading"),
            LOADED(ORIGINAL.path + "loaded");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum PLAYER_MANAGER {
            ORIGINAL(MANAGER.ORIGINAL.path + "playerManager."),
            LOADING(ORIGINAL.path + "loading"),
            LOADED(ORIGINAL.path + "loaded");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum EVENTS_MANAGER {
            ORIGINAL(MANAGER.ORIGINAL.path + "eventsManager."),
            LOADING(ORIGINAL.path + "loading"),
            LOADED(ORIGINAL.path + "loaded");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum COMMANDS_MANAGER {
            ORIGINAL(MANAGER.ORIGINAL.path + "commandsManager."),
            LOADING(ORIGINAL.path + "loading"),
            LOADED(ORIGINAL.path + "loaded");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum TAB_ANIMATOR {
            ORIGINAL(MANAGER.ORIGINAL.path + "tabAnimator."),
            LOADING(ORIGINAL.path + "loading"),
            LOADED(ORIGINAL.path + "loaded");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum RACE_MANAGER {
            ORIGINAL(MANAGER.ORIGINAL.path + "raceManager."),
            LOADING(ORIGINAL.path + "loading"),
            LOADED(ORIGINAL.path + "loaded");

            private final String path;
        }

        private final String path;
    }

    @AllArgsConstructor
    @Getter
    public enum COMMANDS {
        ORIGINAL("commands."),
        NO_PERMISSION(ORIGINAL.path + "no-permission"),
        PLAYER_ONLY(ORIGINAL.path + "player-only"),
        CONSOLE_ONLY(ORIGINAL.path + "console-only"),
        UNKNOWN_COMMAND(ORIGINAL.path + "unknown-command"),
        INVALID_ARGUMENTS(ORIGINAL.path + "invalid-arguments"),
        DISPLAY_MONEY(ORIGINAL.path + "display-money");

        // ── CozyHouseCore help ────────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum HELP {
            ORIGINAL(COMMANDS.ORIGINAL.path + "help."),
            HEADER(ORIGINAL.path + "header"),
            TITLE(ORIGINAL.path + "title"),
            EMPTY_LINE(ORIGINAL.path + "empty-line"),
            FOOTER(ORIGINAL.path + "footer"),
            LINES(ORIGINAL.path + "lines");

            private final String path;
        }

        // ── RPG : général ──────────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum RPG {
            ORIGINAL(COMMANDS.ORIGINAL.path + "rpg."),
            PLAYER_NOT_FOUND(ORIGINAL.path + "player-not-found"),
            INVALID_AMOUNT(ORIGINAL.path + "invalid-amount"),
            UNKNOWN_ACTION(ORIGINAL.path + "unknown-action");

            private final String path;
        }

        // ── RPG : aide (/chrpg) ───────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum RPG_HELP {
            ORIGINAL(COMMANDS.ORIGINAL.path + "rpg.help."),
            HEADER(ORIGINAL.path + "header"),
            TITLE(ORIGINAL.path + "title"),
            EMPTY_LINE(ORIGINAL.path + "empty-line"),
            FOOTER(ORIGINAL.path + "footer"),
            LINES(ORIGINAL.path + "lines");

            private final String path;
        }

        // ── RPG : expérience ──────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum RPG_EXP {
            ORIGINAL(COMMANDS.ORIGINAL.path + "rpg.exp."),
            USAGE(ORIGINAL.path + "usage"),
            ALREADY_MAX(ORIGINAL.path + "already-max"),
            GIVE_SUCCESS_ADMIN(ORIGINAL.path + "give-success-admin"),
            GIVE_SUCCESS_PLAYER(ORIGINAL.path + "give-success-player"),
            SET_SUCCESS_ADMIN(ORIGINAL.path + "set-success-admin"),
            SET_SUCCESS_PLAYER(ORIGINAL.path + "set-success-player");

            private final String path;
        }

        // ── RPG : niveau ──────────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum RPG_LEVEL {
            ORIGINAL(COMMANDS.ORIGINAL.path + "rpg.level."),
            USAGE(ORIGINAL.path + "usage"),
            CAPPED(ORIGINAL.path + "capped"),
            GIVE_SUCCESS_ADMIN(ORIGINAL.path + "give-success-admin"),
            GIVE_SUCCESS_PLAYER(ORIGINAL.path + "give-success-player"),
            SET_SUCCESS_ADMIN(ORIGINAL.path + "set-success-admin"),
            SET_SUCCESS_PLAYER(ORIGINAL.path + "set-success-player");

            private final String path;
        }

        // ── RPG : stats ───────────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum RPG_STATS {
            ORIGINAL(COMMANDS.ORIGINAL.path + "rpg.stats."),
            HEADER(ORIGINAL.path + "header"),
            TITLE(ORIGINAL.path + "title"),
            FOOTER(ORIGINAL.path + "footer"),
            EMPTY_LINE(ORIGINAL.path + "empty-line"),
            RACE_LEVEL_XP(ORIGINAL.path + "race-level-xp"),
            SKILL_POINTS(ORIGINAL.path + "skill-points"),
            HEALTH(ORIGINAL.path + "health"),
            ATTRIBUTES_HEADER(ORIGINAL.path + "attributes-header"),
            STRENGTH(ORIGINAL.path + "strength"),
            DEXTERITY(ORIGINAL.path + "dexterity"),
            CONSTITUTION(ORIGINAL.path + "constitution"),
            INTELLIGENCE(ORIGINAL.path + "intelligence"),
            CHARISMA(ORIGINAL.path + "charisma"),
            COMBAT_HEADER(ORIGINAL.path + "combat-header"),
            ATTACK_DAMAGE(ORIGINAL.path + "attack-damage"),
            ATTACK_SPEED(ORIGINAL.path + "attack-speed"),
            ARMOR(ORIGINAL.path + "armor"),
            TOUGHNESS(ORIGINAL.path + "toughness"),
            CRIT_CHANCE(ORIGINAL.path + "crit-chance"),
            CRIT_DAMAGE(ORIGINAL.path + "crit-damage"),
            DODGE(ORIGINAL.path + "dodge"),
            KB_RESISTANCE(ORIGINAL.path + "kb-resistance"),
            BONUSES_HEADER(ORIGINAL.path + "bonuses-header"),
            EXP_BONUS(ORIGINAL.path + "exp-bonus"),
            LOOT_BONUS(ORIGINAL.path + "loot-bonus"),
            TRADE_DISCOUNT(ORIGINAL.path + "trade-discount"),
            POISON_RESIST(ORIGINAL.path + "poison-resist"),
            FALL_REDUCE(ORIGINAL.path + "fall-reduce"),
            SWIM_SPEED(ORIGINAL.path + "swim-speed");

            private final String path;
        }

        private final String path;
    }

    @AllArgsConstructor
    @Getter
    public enum PROGRAMERRORS {
        ORIGINAL("programErrors."),
        PERSISTANT_DATA_MISSING(ORIGINAL.path + "persistant-data-missing"),
        GAME_PLAYER_NOT_INITIALIZE(ORIGINAL.path + "game-player-not-initialize"),
        RACE_NOT_SELECTED(ORIGINAL.path + "race-not-selected"),
        RACE_MANAGER_NOT_INITIALIZED(ORIGINAL.path + "race-manager-not-initialized"),
        ERROR_OCCURRED(ORIGINAL.path + "error-occurred");

        private final String path;
    }

    @AllArgsConstructor
    @Getter
    public enum RACE {
        ORIGINAL("race.");

        // ── Sélection de race ──────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum SELECTION {
            ORIGINAL(RACE.ORIGINAL.path + "selection."),
            TITLE(ORIGINAL.path + "title"),
            SELECTED(ORIGINAL.path + "selected"),
            DESCRIPTION(ORIGINAL.path + "description"),
            NO_RACE(ORIGINAL.path + "no-race"),
            USE_COMMAND(ORIGINAL.path + "use-command");

            // ── GUI de sélection ─────────────────────────────────────────────
            @AllArgsConstructor
            @Getter
            public enum GUI {
                ORIGINAL(RACE.ORIGINAL.path + "selection.gui."),
                STATS_LABEL(ORIGINAL.path + "stats-label"),
                EMPTY_LINE(ORIGINAL.path + "empty-line"),
                HEALTH(ORIGINAL.path + "health"),
                STRENGTH(ORIGINAL.path + "strength"),
                DEXTERITY(ORIGINAL.path + "dexterity"),
                CONSTITUTION(ORIGINAL.path + "constitution"),
                INTELLIGENCE(ORIGINAL.path + "intelligence"),
                ABILITY_EXP_BONUS(ORIGINAL.path + "ability-exp-bonus"),
                ABILITY_LOOT_BONUS(ORIGINAL.path + "ability-loot-bonus"),
                ABILITY_WATER_BREATHING(ORIGINAL.path + "ability-water-breathing"),
                ABILITY_CLIMB_WALLS(ORIGINAL.path + "ability-climb-walls"),
                ABILITY_NO_FALL(ORIGINAL.path + "ability-no-fall"),
                ABILITY_SUNLIGHT_WEAKNESS(ORIGINAL.path + "ability-sunlight-weakness"),
                CLICK_TO_CHOOSE(ORIGINAL.path + "click-to-choose");

                private final String path;
            }

            private final String path;
        }

        // ── Infos de race ──────────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum INFO {
            ORIGINAL(RACE.ORIGINAL.path + "info."),
            HEADER(ORIGINAL.path + "header"),
            YOUR_RACE(ORIGINAL.path + "your-race"),
            DESCRIPTION_LABEL(ORIGINAL.path + "description-label"),
            EMPTY_LINE(ORIGINAL.path + "empty-line"),
            STATS_HEADER(ORIGINAL.path + "stats-header"),
            LEVEL_XP(ORIGINAL.path + "level-xp"),
            HEALTH(ORIGINAL.path + "health"),
            STATS_LINE(ORIGINAL.path + "stats-line"),
            FOOTER(ORIGINAL.path + "footer");

            private final String path;
        }

        // ── Races individuelles ────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum HUMAN {
            ORIGINAL(RACE.ORIGINAL.path + "human."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum ELF {
            ORIGINAL(RACE.ORIGINAL.path + "elf."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum DWARF {
            ORIGINAL(RACE.ORIGINAL.path + "dwarf."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum ORC {
            ORIGINAL(RACE.ORIGINAL.path + "orc."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum GOBLIN {
            ORIGINAL(RACE.ORIGINAL.path + "goblin."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum FAIRY {
            ORIGINAL(RACE.ORIGINAL.path + "fairy."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum BEAST {
            ORIGINAL(RACE.ORIGINAL.path + "beast."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        @AllArgsConstructor
        @Getter
        public enum FISH {
            ORIGINAL(RACE.ORIGINAL.path + "fish."),
            DISPLAY_NAME(ORIGINAL.path + "display-name"),
            DESCRIPTION(ORIGINAL.path + "description");

            private final String path;
        }

        private final String path;
    }

    @AllArgsConstructor
    @Getter
    public enum RPG {
        ORIGINAL("rpg.");

        // ── Expérience ─────────────────────────────────────────────────────────
        @AllArgsConstructor
        @Getter
        public enum EXPERIENCE {
            ORIGINAL(RPG.ORIGINAL.path + "experience."),
            LEVEL_MAX_DISPLAY(ORIGINAL.path + "level-max-display"),
            XP_MAX_DISPLAY(ORIGINAL.path + "xp-max-display"),
            LEVEL_UP(ORIGINAL.path + "level-up"),
            MAX_LEVEL_REACHED(ORIGINAL.path + "max-level-reached"),
            ACTION_BAR(ORIGINAL.path + "action-bar");

            private final String path;
        }

        private final String path;
    }

    // ── Tessères (CozyHouseRPG) ──────────────────────────────────────────────────
    @AllArgsConstructor
    @Getter
    public enum TESSERE {
        ORIGINAL("tessere.");

        /**
         * Loadout messages (revelation, engraving)
         */
        @AllArgsConstructor
        @Getter
        public enum LOADOUT {
            ORIGINAL(TESSERE.ORIGINAL.path + "loadout."),
            ENGRAVING_UNCHANGED(ORIGINAL.path + "engraving-unchanged"),
            EQUIPPED(ORIGINAL.path + "equipped"),
            REVELATION_SEPARATOR(ORIGINAL.path + "revelation-separator"),
            REVELATION_TITLE(ORIGINAL.path + "revelation-title"),
            REVELATION_LINE(ORIGINAL.path + "revelation-line"),
            SCORE_PERFECT(ORIGINAL.path + "score-perfect"),
            SCORE_HIGH(ORIGINAL.path + "score-high"),
            SCORE_AVERAGE(ORIGINAL.path + "score-average"),
            SCORE_LOW(ORIGINAL.path + "score-low"),
            SCORE_CHAOTIC(ORIGINAL.path + "score-chaotic");

            private final String path;
        }

        /**
         * GUI labels and item names for Tessere inventories
         */
        @AllArgsConstructor
        @Getter
        public enum GUI {
            ORIGINAL(TESSERE.ORIGINAL.path + "gui."),
            EMPTY_SLOT_NAME(ORIGINAL.path + "empty-slot-name"),
            EMPTY_SLOT_NODE(ORIGINAL.path + "empty-slot-node"),
            SLOT_CLICK_CHOOSE(ORIGINAL.path + "slot-click-choose"),
            SLOT_CLICK_REMOVE(ORIGINAL.path + "slot-click-remove"),
            SLOT_EFFICIENCY(ORIGINAL.path + "slot-efficiency"),
            SLOT_AFFINITY(ORIGINAL.path + "slot-affinity"),
            VALIDATE_INCOMPLETE_NAME(ORIGINAL.path + "validate-incomplete-name"),
            VALIDATE_INCOMPLETE_LORE(ORIGINAL.path + "validate-incomplete-lore"),
            VALIDATE_READY_NAME(ORIGINAL.path + "validate-ready-name"),
            VALIDATE_READY_LORE(ORIGINAL.path + "validate-ready-lore"),
            VALIDATE_ACTIVE_NAME(ORIGINAL.path + "validate-active-name"),
            VALIDATE_ACTIVE_SCORE(ORIGINAL.path + "validate-active-score"),
            VALIDATE_ACTIVE_ABILITY(ORIGINAL.path + "validate-active-ability"),
            CATALOG_EFFICIENCY(ORIGINAL.path + "catalog-efficiency"),
            CATALOG_AFFINITY(ORIGINAL.path + "catalog-affinity"),
            EFF_EXCELLENT(ORIGINAL.path + "eff-excellent"),
            EFF_GOOD(ORIGINAL.path + "eff-good"),
            EFF_WEAK(ORIGINAL.path + "eff-weak"),
            LOCKED_NAME(ORIGINAL.path + "locked-name"),
            LOCKED_LORE(ORIGINAL.path + "locked-lore");

            private final String path;
        }

        /**
         * Combat effects triggered by Tesseres
         */
        @AllArgsConstructor
        @Getter
        public enum COMBAT {
            ORIGINAL(TESSERE.ORIGINAL.path + "combat."),
            CHAOS_SURGE(ORIGINAL.path + "chaos-surge"),
            CHAOS_DISSIPATION(ORIGINAL.path + "chaos-dissipation");

            private final String path;
        }

        /**
         * Skill mode messages
         */
        @AllArgsConstructor
        @Getter
        public enum SKILL_MODE {
            ORIGINAL(TESSERE.ORIGINAL.path + "skill-mode."),
            ENTER(ORIGINAL.path + "enter"),
            EXIT(ORIGINAL.path + "exit"),
            NO_SKILLS(ORIGINAL.path + "no-skills"),
            ACTIVATED(ORIGINAL.path + "activated"),
            SELECTED(ORIGINAL.path + "selected"),
            PASSIVE_ONLY(ORIGINAL.path + "passive-only"),
            ON_COOLDOWN(ORIGINAL.path + "on-cooldown");

            private final String path;
        }

        /**
         * Messages for /chrpg tesseres subcommands
         */
        @AllArgsConstructor
        @Getter
        public enum COMMANDS {
            ORIGINAL(TESSERE.ORIGINAL.path + "commands."),
            DISCOVER_SUCCESS(ORIGINAL.path + "discover-success"),
            DISCOVER_ALL_SUCCESS(ORIGINAL.path + "discover-all-success"),
            RESET_SUCCESS(ORIGINAL.path + "reset-success"),
            INFO_SEPARATOR(ORIGINAL.path + "info-separator"),
            INFO_TITLE(ORIGINAL.path + "info-title"),
            INFO_SLOT_LINE(ORIGINAL.path + "info-slot-line"),
            INFO_SLOT_EMPTY(ORIGINAL.path + "info-slot-empty"),
            INFO_NOT_REVEALED(ORIGINAL.path + "info-not-revealed"),
            INFO_SCORE(ORIGINAL.path + "info-score"),
            INFO_ABILITY(ORIGINAL.path + "info-ability");

            private final String path;
        }

        private final String path;
    }

    @AllArgsConstructor
    @Getter
    public enum BIOME {
        ORIGINAL("biome."),
        CHANGED(ORIGINAL.path + "changed"),
        ENTERING(ORIGINAL.path + "entering");

        private final String path;
    }
}
