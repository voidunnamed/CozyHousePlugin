package fr.cozyhouse.cozyHouseRPG.skills.gui;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import fr.cozyhouse.cozyHouseRPG.skills.playerstate.TessereLoadout;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TessereBodyGUI {

    // Titre constant utilisé par InventoryClickListener pour identifier ce GUI.
    public static final String TITRE = "§5§l✦ Gravure Arcane";
    public static final int SLOT_VALIDER = 40;

    private final PluginContext context;

    public TessereBodyGUI(PluginContext context) {
        this.context = context;
    }

    public void ouvrir(RPGPlayer rpg) {
        Inventory inv = Bukkit.createInventory(null, 45, TITRE);
        MessageManager msg = context.getMessageManager();

        // Fond violet
        ItemStack verre = makeItem(Material.PURPLE_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 45; i++) inv.setItem(i, verre);

        // 6 emplacements
        for (TessereSlot slot : TessereSlot.values())
            inv.setItem(slot.getGuiSlotIndex(), itemSlot(slot, rpg, msg));

        // Bouton Valider
        inv.setItem(SLOT_VALIDER, itemValider(rpg, msg));

        // 3 compétences révélées en haut (slots 2, 4, 6)
        TessereLoadout loadout = rpg.getTessereLoadout();
        if (loadout.isRevealed()) {
            int[] pos = {2, 4, 6};
            List<Skill> comp = loadout.getActiveSkills();
            for (int i = 0; i < comp.size(); i++)
                inv.setItem(pos[i], comp.get(i).getIcon());
        }

        rpg.getBukkitPlayer().openInventory(inv);
    }

    private ItemStack itemSlot(TessereSlot slot, RPGPlayer rpg, MessageManager msg) {
        Tessere equipee = rpg.getTessereLoadout().getEquippedFragments().get(slot);

        if (equipee == null) {
            return makeItem(Material.PURPLE_STAINED_GLASS,
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.EMPTY_SLOT_NAME.getPath(),
                            "%slot%", slot.getDisplayName()),
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.EMPTY_SLOT_NODE.getPath(),
                            "%node%", slot.getNodeLabel()),
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.SLOT_CLICK_CHOOSE.getPath()),
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.SLOT_CLICK_REMOVE.getPath()));
        }

        // Tessère équipée — ajouter efficacité et affinité dans le lore
        ItemStack item = equipee.buildItem(true);
        if (equipee.getType() != null) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.SLOT_EFFICIENCY.getPath(),
                    "%value%", TessereType.labelEff(equipee.getType().getSlotEfficiency(slot), msg)));
            if (rpg.hasRace())
                lore.add(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.SLOT_AFFINITY.getPath(),
                        "%race%",  rpg.getCurrentRace().getDisplayName(),
                        "%value%", TessereType.labelEff(equipee.getType().getRaceAffinity(rpg.getCurrentRace()), msg)));
            lore.add(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.SLOT_CLICK_REMOVE.getPath()));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack itemValider(RPGPlayer rpg, MessageManager msg) {
        TessereLoadout loadout = rpg.getTessereLoadout();

        if (!loadout.isComplete())
            return makeItem(Material.RED_CONCRETE,
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.VALIDATE_INCOMPLETE_NAME.getPath()),
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.VALIDATE_INCOMPLETE_LORE.getPath(),
                            "%count%", String.valueOf(loadout.filledSlotCount())));

        if (loadout.isDirty() || !loadout.isRevealed())
            return makeItem(Material.LIME_CONCRETE,
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.VALIDATE_READY_NAME.getPath()),
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.VALIDATE_READY_LORE.getPath()));

        // Already validated, not modified
        List<String> lore = new ArrayList<>();
        lore.add(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.VALIDATE_ACTIVE_SCORE.getPath(),
                "%value%", String.format("%.0f%%", loadout.getEfficaciteScore() * 100)));
        loadout.getActiveSkills().forEach(s ->
                lore.add(msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.VALIDATE_ACTIVE_ABILITY.getPath(),
                        "%nom%", s.getDisplayName())));
        return makeItem(Material.GOLD_BLOCK,
                msg.getMessageNoPrefix(MessagesEnum.TESSERE.GUI.VALIDATE_ACTIVE_NAME.getPath()),
                lore.toArray(new String[0]));
    }

    private ItemStack makeItem(Material mat, String nom, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(nom);
        meta.setLore(Arrays.asList(loreLines));
        item.setItemMeta(meta);
        return item;
    }
}
