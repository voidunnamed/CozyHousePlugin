package fr.cozyhouse.cozyHouseRPG.gui;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.race.Race;
import fr.cozyhouse.cozyHouseRPG.race.RaceRegistry;
import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI de sélection de race.
 * Crée et affiche l'inventaire avec toutes les races disponibles.
 * Tous les textes sont lus depuis le stringMessages.yml de CozyHouse.
 */
@RequiredArgsConstructor
public class RaceSelectionGUI {

    private final PluginContext context;

    /**
     * Retourne le titre traduit de l'inventaire.
     * Utilisé à la fois pour créer l'inventaire et pour le vérifier dans le listener.
     */
    public String getInventoryTitle() {
        return context.getMessageManager()
                .getMessageNoPrefix(MessagesEnum.RACE.SELECTION.TITLE.getPath());
    }

    /**
     * Ouvre l'inventaire de sélection de race pour un joueur.
     */
    public void open(Player player) {
        Inventory inventory = createInventory();
        player.openInventory(inventory);
    }

    /**
     * Crée l'inventaire de sélection de race.
     */
    private Inventory createInventory() {
        RaceType[] raceTypes = RaceType.values();
        int size = ((raceTypes.length - 1) / 9 + 1) * 9;

        Inventory inventory = Bukkit.createInventory(null, size, getInventoryTitle());

        RaceRegistry registry = context.getRaceRegistry();

        for (int i = 0; i < raceTypes.length; i++) {
            RaceType type = raceTypes[i];
            Race race = registry.getRace(type);
            inventory.setItem(i, createRaceItem(race));
        }

        return inventory;
    }

    /**
     * Crée un ItemStack pour une race, avec son lore complet issu du yml.
     */
    private ItemStack createRaceItem(Race race) {
        ItemStack item = race.getIconCopy();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        MessageManager msg = context.getMessageManager();

        // Nom affiché avec couleur de la race
        meta.setDisplayName(race.getChatColor() + "§l" + race.getDisplayName());

        // Lore
        List<String> lore = new ArrayList<>();
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.DESCRIPTION.getPath(),
                "%description%", race.getDescription()));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.EMPTY_LINE.getPath()));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.STATS_LABEL.getPath()));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.HEALTH.getPath(),
                "%value%", String.format("%.1f", race.getBaseHealth())));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.STRENGTH.getPath(),
                "%value%", String.format("%.1f", race.getBaseStrength())));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.DEXTERITY.getPath(),
                "%value%", String.format("%.1f", race.getBaseDexterity())));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.CONSTITUTION.getPath(),
                "%value%", String.format("%.1f", race.getBaseConstitution())));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.INTELLIGENCE.getPath(),
                "%value%", String.format("%.1f", race.getBaseIntelligence())));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.EMPTY_LINE.getPath()));

        addAbilityLore(lore, race, msg);

        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.EMPTY_LINE.getPath()));
        lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.CLICK_TO_CHOOSE.getPath()));

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Ajoute les lignes de compétences spéciales au lore.
     */
    private void addAbilityLore(List<String> lore, Race race, MessageManager msg) {
        if (race.getExpBonus() > 0) {
            lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.ABILITY_EXP_BONUS.getPath(),
                    "%value%", String.format("%.0f", race.getExpBonus() * 100)));
        }
        if (race.getLootBonus() > 0) {
            lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.ABILITY_LOOT_BONUS.getPath(),
                    "%value%", String.format("%.0f", race.getLootBonus() * 100)));
        }
        if (race.isWaterBreathing()) {
            lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.ABILITY_WATER_BREATHING.getPath()));
        }
        if (race.isCanClimbWalls()) {
            lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.ABILITY_CLIMB_WALLS.getPath()));
        }
        if (race.isNoFallDamage()) {
            lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.ABILITY_NO_FALL.getPath()));
        }
        if (race.isSunlightWeakness()) {
            lore.add(msg.getMessageNoPrefix(MessagesEnum.RACE.SELECTION.GUI.ABILITY_SUNLIGHT_WEAKNESS.getPath()));
        }
    }
}
