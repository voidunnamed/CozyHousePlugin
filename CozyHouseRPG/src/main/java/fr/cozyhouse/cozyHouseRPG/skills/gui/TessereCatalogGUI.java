package fr.cozyhouse.cozyHouseRPG.skills.gui;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TessereCatalogGUI {

    // Prefix constant used by InventoryClickListener to identify this GUI.
    public static final String PREFIX_TITRE = "§5§l✦ Tessère — ";

    // PDC key stored in each item to identify the TessereType on click.
    // Only set on DISCOVERED Tesseres — locked slots have no PDC key, so clicks are ignored.
    public static final NamespacedKey CLE_TYPE = new NamespacedKey("cozyhouserpg", "tessere_type");

    private final PluginContext context;

    public TessereCatalogGUI(PluginContext context) {
        this.context = context;
    }

    public void ouvrir(RPGPlayer rpg, TessereSlot slotCible) {
        Inventory inv = Bukkit.createInventory(null, 54, PREFIX_TITRE + slotCible.getDisplayName());
        MessageManager msg = context.getMessageManager();

        int i = 0;
        for (TessereType type : TessereType.values()) {

            ItemStack item;

            if (rpg.getTessereLoadout().isDiscovered(type)) {
                // ── Discovered: show full info and make selectable ──────────────
                Tessere tessere = type.buildTessere();
                item = tessere.buildItem(false);

                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore() != null
                        ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add("");
                lore.add(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.CATALOG_EFFICIENCY.getPath(),
                        "%value%", TessereType.labelEff(type.getSlotEfficiency(slotCible), msg)));
                if (rpg.hasRace())
                    lore.add(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.CATALOG_AFFINITY.getPath(),
                            "%race%",  rpg.getCurrentRace().getDisplayName(),
                            "%value%", TessereType.labelEff(type.getRaceAffinity(rpg.getCurrentRace()), msg)));

                // Store enum name in PDC → makes this slot selectable
                meta.getPersistentDataContainer().set(CLE_TYPE, PersistentDataType.STRING, type.name());
                meta.setLore(lore);
                item.setItemMeta(meta);

            } else {
                // ── Not discovered: show locked slot ────────────────────────────
                item = buildLockedItem(msg);
                // No PDC key → click will be ignored by InventoryClickListener
            }

            inv.setItem(i++, item);
        }

        rpg.getBukkitPlayer().openInventory(inv);
    }

    /** Builds the item displayed for an undiscovered Tessere. */
    private ItemStack buildLockedItem(MessageManager msg) {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.LOCKED_NAME.getPath()));
        meta.setLore(Collections.singletonList(
                msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.LOCKED_LORE.getPath())
        ));
        item.setItemMeta(meta);
        return item;
    }

    /** Reads the TessereType stored in a clicked item. Returns null if not a Tessere item. */
    public static TessereType lireType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String nom = item.getItemMeta().getPersistentDataContainer()
                .get(CLE_TYPE, PersistentDataType.STRING);
        if (nom == null) return null;
        try { return TessereType.valueOf(nom); } catch (IllegalArgumentException e) { return null; }
    }
}
