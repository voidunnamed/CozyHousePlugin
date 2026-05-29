package fr.cozyhouse.cozyHouseRPG.skills.playerstate;

import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TessereLoadout {

    /** Tesseres engraved per slot. null = empty slot. */
    private final Map<TessereSlot, Tessere> equippedFragments = new EnumMap<>(TessereSlot.class);

    /** Tesseres the player has discovered and can select in the catalog. */
    private final Set<TessereType> discoveredTesseres = new HashSet<>();

    /** The 3 revealed active skills. Empty until the player validates. */
    private List<Skill> activeSkills = new ArrayList<>();

    /** True if skills have been revealed for the current configuration. */
    private boolean revealed = false;

    /** True if the configuration changed since the last reveal. */
    private boolean dirty = false;

    /**
     * Global resonance score: sum of all pair compatibilities.
     * Range: 0 (fully discordant) to 75 (perfect harmony for 6 Tesseres).
     */
    private int resonanceScore = 0;

    /** Global efficiency score (0.0 → 1.0). Influences skill power. */
    private double efficaciteScore = 0.0;

    public TessereLoadout() {
        for (TessereSlot slot : TessereSlot.values()) {
            equippedFragments.put(slot, null);
        }
    }

    // ── Engraving ─────────────────────────────────────────────────────────────

    public void equipFragment(TessereSlot slot, Tessere fragment) {
        equippedFragments.put(slot, fragment);
        dirty = true;
        revealed = false;
    }

    public void unequipFragment(TessereSlot slot) {
        equippedFragments.put(slot, null);
        dirty = true;
        revealed = false;
    }

    public int filledSlotCount() {
        return (int) equippedFragments.values().stream().filter(Objects::nonNull).count();
    }

    public boolean isComplete() { return filledSlotCount() == 6; }

    public void revealSkills(List<Skill> skills, int resonance, double efficaciteScore) {
        this.activeSkills    = new ArrayList<>(skills);
        this.resonanceScore  = resonance;
        this.efficaciteScore = efficaciteScore;
        this.revealed        = true;
        this.dirty           = false;
    }

    public List<Tessere> getEquippedFragmentList() {
        return equippedFragments.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ── Discovery ─────────────────────────────────────────────────────────────

    /** Marks a single Tessere as discovered. */
    public void discover(TessereType type) {
        discoveredTesseres.add(type);
    }

    /** Marks all Tesseres as discovered. */
    public void discoverAll() {
        Collections.addAll(discoveredTesseres, TessereType.values());
    }

    /** Returns true if the player has discovered this Tessere. */
    public boolean isDiscovered(TessereType type) {
        return discoveredTesseres.contains(type);
    }

    /** Returns an unmodifiable view of discovered Tesseres. */
    public Set<TessereType> getDiscoveredTesseres() {
        return Collections.unmodifiableSet(discoveredTesseres);
    }
}
