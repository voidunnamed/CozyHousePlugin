package fr.cozyhouse.cozyHouseRPG.listeners;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.gui.RaceSelectionGUI;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.race.Race;
import fr.cozyhouse.cozyHouseRPG.race.RaceRegistry;
import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType;
import fr.cozyhouse.cozyHouseRPG.skills.gui.TessereBodyGUI;
import fr.cozyhouse.cozyHouseRPG.skills.gui.TessereCatalogGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Listener unique pour InventoryClickEvent.
 *
 * Centralise la gestion de tous les clics d'inventaire du plugin.
 * Délègue à chaque sous-système selon le titre du GUI ouvert :
 *   - Sélection de race (RaceSelectionGUI)
 *   - Gravure principale des Tessères (TessereBodyGUI)
 *   - Catalogue de sélection des Tessères (TessereCatalogGUI)
 *
 * Pour ajouter un nouveau GUI : ajouter une condition dans onInventoryClick()
 * et créer une méthode privée handleXxx().
 */
public class InventoryClickListener implements Listener {

    private final PluginContext context;
    /** Titre du GUI de sélection de race, mis en cache à la construction. */
    private final String raceTitre;

    public InventoryClickListener(PluginContext context) {
        this.context = context;
        this.raceTitre = new RaceSelectionGUI(context).getInventoryTitle();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String titre = event.getView().getTitle();

        // ── Sélection de race ──────────────────────────────────────────────────
        if (titre.equals(raceTitre)) {
            handleRaceSelection(event, player);
            return;
        }

        // ── Gravure principale ─────────────────────────────────────────────────
        if (titre.equals(TessereBodyGUI.TITRE)) {
            handleTessereBody(event, player);
            return;
        }

        // ── Catalogue des Tessères ─────────────────────────────────────────────
        if (titre.startsWith(TessereCatalogGUI.PREFIX_TITRE)) {
            handleTessereCatalogue(event, player, titre);
        }
    }

    // ── Sélection de race ──────────────────────────────────────────────────────

    private void handleRaceSelection(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        int slot = event.getSlot();
        RaceType[] raceTypes = RaceType.values();
        if (slot < 0 || slot >= raceTypes.length) return;

        RaceType selectedRaceType = raceTypes[slot];
        MessageManager msg = context.getMessageManager();

        RPGPlayer rpgPlayer = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpgPlayer == null) {
            msg.send(player, MessagesEnum.PROGRAMERRORS.ERROR_OCCURRED.getPath());
            return;
        }

        RaceRegistry raceRegistry = context.getRaceRegistry();
        Race race = raceRegistry.getRace(selectedRaceType);

        context.getPlayerService().setPlayerRace(rpgPlayer, selectedRaceType);

        msg.sendNoPrefix(player, MessagesEnum.RACE.SELECTION.SELECTED.getPath(),
                "%race%", race.getChatColor() + race.getDisplayName());
        msg.sendNoPrefix(player, MessagesEnum.RACE.SELECTION.DESCRIPTION.getPath(),
                "%description%", race.getDescription());

        player.closeInventory();
    }

    // ── Gravure principale (TessereBodyGUI) ────────────────────────────────────

    private void handleTessereBody(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        RPGPlayer rpg = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null) return;

        int slot = event.getSlot();

        // Bouton Valider
        if (slot == TessereBodyGUI.SLOT_VALIDER) {
            if (rpg.getTessereLoadout().isComplete()) {
                context.getTessereService().revelerCompetences(rpg);
                new TessereBodyGUI(context).ouvrir(rpg);
            }
            return;
        }

        // Clic sur un emplacement de slot
        for (TessereSlot ts : TessereSlot.values()) {
            if (slot != ts.getGuiSlotIndex()) continue;
            if (event.isLeftClick())
                new TessereCatalogGUI(context).ouvrir(rpg, ts);
            else if (event.isRightClick()) {
                context.getTessereService().retirer(rpg, ts);
                new TessereBodyGUI(context).ouvrir(rpg);
            }
            return;
        }
    }

    // ── Catalogue des Tessères (TessereCatalogGUI) ─────────────────────────────

    private void handleTessereCatalogue(InventoryClickEvent event, Player player, String titre) {
        event.setCancelled(true);

        RPGPlayer rpg = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null) return;

        // Retrouver le slot cible depuis le titre du GUI
        String nomSlot = titre.replace(TessereCatalogGUI.PREFIX_TITRE, "");
        TessereSlot slotCible = Arrays.stream(TessereSlot.values())
                .filter(s -> s.getDisplayName().equals(nomSlot))
                .findFirst().orElse(null);
        if (slotCible == null) return;

        // Lire la Tessère cliquée depuis son PDC
        TessereType type = TessereCatalogGUI.lireType(event.getCurrentItem());
        if (type == null) return;

        context.getTessereService().equiper(rpg, slotCible, type);
        new TessereBodyGUI(context).ouvrir(rpg);
    }
}
