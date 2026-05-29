package fr.cozyhouse.cozyHouseRPG.skills.display;

import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereRarity;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType;
import fr.cozyhouse.cozyHouseRPG.skills.logic.TessereCalcul;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Tessere {

    private final String DisplayName;
    private final String Description;
    private final String LoreQuote;
    private final ChatColor color;
    private final TessereType type;
    private final TessereRarity rarity;
    private final Material iconMaterial;
    private final TessereCalcul tessereCalcul;

    public ItemStack buildItem(boolean isEquipped) {

        // ── 1. Créer l'ItemStack de base ──────────────────────────────────────
        ItemStack item = new ItemStack(iconMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // ── 2. Nom de la Tessère (coloré selon la couleur définie) ────────────
        meta.setDisplayName(color + "" + ChatColor.BOLD + DisplayName);

        // ── 3. Construction du Lore (la liste de textes sous le nom) ─────────
        List<String> lore = new ArrayList<>();

        // Description
        lore.add(ChatColor.GRAY + Description);
        lore.add(""); // ligne vide = séparateur

        // Citation lore en italique
        lore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "\"" + LoreQuote + "\"");
        lore.add("");

        // Signature arcanique : Charge / Entropie / Flux
        String chargeLabel = switch (tessereCalcul.getCharge()) {
            case POSITIVE  -> ChatColor.GREEN  + "(+)";
            case NEUTRAL   -> ChatColor.YELLOW + "(∅)";
            case NEGATIVE  -> ChatColor.RED    + "(-)";
        };
        String entropyLabel = switch (tessereCalcul.getEntropy()) {
            case ORDER   -> ChatColor.AQUA    + "[O]";
            case BALANCE -> ChatColor.YELLOW  + "[E]";
            case CHAOS   -> ChatColor.DARK_RED + "[C]";
        };
        String flowLabel = switch (tessereCalcul.getFlow()) {
            case ASCENDING   -> ChatColor.GREEN  + "↑";
            case STABLE      -> ChatColor.YELLOW + "→";
            case DESCENDING  -> ChatColor.RED    + "↓";
        };
        lore.add(ChatColor.GRAY + "⚡ Signature : " + chargeLabel + ChatColor.GRAY + " / " + entropyLabel + ChatColor.GRAY + " / " + flowLabel);

        lore.add(ChatColor.DARK_GRAY + "Rareté : " + rarity.getDisplayName());

        // Domaine de la Tessère
        lore.add(ChatColor.GRAY + "⬡ Domaine : " + ChatColor.WHITE + tessereCalcul.getDomaines().name());

        // Indicateur si actuellement gravée
        if (isEquipped) {
            lore.add("");
            lore.add(ChatColor.GREEN + "✔ Actuellement gravée");
        }

        // Compétences potentielles : toujours cachées
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "??? Se révèle après gravure");

        // ── 4. Appliquer le lore et retourner l'item ──────────────────────────
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
