package fr.cozyhouse.cozyHouseRPG.skills.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * The Arcane Wand — the item used to enter and exit skill mode.
 *
 * Right-clicking it while having revealed skills:
 *   - enters skill mode  (inventory saved, skills placed in hotbar)
 *   - or exits skill mode (inventory restored) if already in skill mode
 *
 * Identified by a PDC key so it survives rename/lore changes.
 */
public class ArcaneWand {

    /** PDC key stored on the wand item. Presence = this is a wand. */
    public static final NamespacedKey KEY = new NamespacedKey("cozyhouserpg", "arcane_wand");

    private ArcaneWand() {}

    /** Builds a fresh Arcane Wand ItemStack. */
    public static ItemStack build() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "✦ Arcane Wand");
        meta.setLore(List.of(
                ChatColor.GRAY + "Channels your engraved Tessere power.",
                "",
                ChatColor.DARK_GRAY + "Right-click to enter Skill Mode."
        ));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    /** Returns true if the given item is an Arcane Wand. */
    public static boolean isWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta()
                .getPersistentDataContainer()
                .has(KEY, PersistentDataType.BYTE);
    }
}
