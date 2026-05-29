package fr.cozyhouse.cozyHouseRPG.skills.logic;

import fr.cozyhouse.cozyHouse.MessageManager;
import fr.cozyhouse.cozyHouse.messages.MessagesEnum;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.SkillType;
import fr.cozyhouse.cozyHouseRPG.skills.datas.Domaines;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSkill;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereType;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import fr.cozyhouse.cozyHouseRPG.skills.playerstate.TessereLoadout;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class TessereService {

    private final PluginContext context;

    public TessereService(PluginContext context) {
        this.context = context;
    }

    // ── Équipement ─────────────────────────────────────────────────────────────

    public void equiper(RPGPlayer rpg, TessereSlot slot, TessereType type) {
        Tessere tessere = type.buildTessere();
        rpg.getTessereLoadout().equipFragment(slot, tessere);
        MessageManager msg = context.getMessageManager();
        double eff = type.getSlotEfficiency(slot);
        rpg.getBukkitPlayer().sendMessage(msg.getMessageNoPrefix(
                MessagesEnum.TESSERE.LOADOUT.EQUIPPED.getPath(),
                "%nom%",  type.getDisplayName(),
                "%slot%", slot.getDisplayName(),
                "%eff%",  TessereType.labelEff(eff, msg)));
    }

    public void retirer(RPGPlayer rpg, TessereSlot slot) {
        rpg.getTessereLoadout().unequipFragment(slot);
    }

    // ── Révélation ─────────────────────────────────────────────────────────────

    public void revelerCompetences(RPGPlayer rpg) {
        TessereLoadout loadout = rpg.getTessereLoadout();
        MessageManager msg = context.getMessageManager();

        if (!loadout.isDirty() && loadout.isRevealed()) {
            rpg.getBukkitPlayer().sendMessage(
                    msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.ENGRAVING_UNCHANGED.getPath()));
            return;
        }

        retirerEffetsPassifs(rpg);

        List<TessereType> equipees = loadout.getEquippedFragmentList().stream()
                .map(Tessere::getType)
                .filter(Objects::nonNull)
                .toList();

        double efficaciteScore = calculerScore(loadout, equipees, rpg.getCurrentRace());
        int resonanceBrut = calculerResonanceBrut(equipees);
        List<Skill> competences = new ArrayList<>(resoudreCompetences(equipees));

        loadout.revealSkills(competences, resonanceBrut, efficaciteScore);

        // Affichage
        Player p = rpg.getBukkitPlayer();
        String sep = msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.REVELATION_SEPARATOR.getPath());
        p.sendMessage(sep);
        p.sendMessage(msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.REVELATION_TITLE.getPath()));
        p.sendMessage(labelScore(efficaciteScore, msg));
        for (int i = 0; i < competences.size(); i++) {
            Skill s = competences.get(i);
            p.sendMessage(msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.REVELATION_LINE.getPath(),
                    "%index%",       String.valueOf(i + 1),
                    "%nom%",         s.getDisplayName(),
                    "%description%", s.getDescription()));
        }
        p.sendMessage(sep);

        appliquerEffetsPassifs(rpg);
    }

    // ── Calcul du score ────────────────────────────────────────────────────────

    private double calculerScore(TessereLoadout loadout, List<TessereType> equipees, RaceType race) {
        double resonance = equipees.size() < 2 ? 0.5
                : (double) calculerResonanceBrut(equipees)
                / (((double)(equipees.size() * (equipees.size() - 1)) / 2) * TessereCalcul.Compatibility.ALFA.number);

        double effSlots = loadout.getEquippedFragments().entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().getType() != null)
                .mapToDouble(e -> e.getValue().getType().getSlotEfficiency(e.getKey()))
                .average().orElse(1.0);

        double affRace = (race != null && !equipees.isEmpty())
                ? equipees.stream().mapToDouble(t -> t.getRaceAffinity(race)).average().orElse(1.0)
                : 1.0;

        double score = (resonance * 0.40) + ((effSlots / 1.5) * 0.30) + ((affRace / 1.5) * 0.30);
        return Math.min(1.0, Math.max(0.0, score));
    }

    private int calculerResonanceBrut(List<TessereType> tesseres) {
        int total = 0;
        for (int i = 0; i < tesseres.size(); i++)
            for (int j = i + 1; j < tesseres.size(); j++)
                total += TessereCalcul.calculResonance(
                        tesseres.get(i).getCalcul(), tesseres.get(j).getCalcul()).number;
        return total;
    }

    private String labelScore(double s, MessageManager msg) {
        if (s >= 0.85) return msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.SCORE_PERFECT.getPath());
        if (s >= 0.65) return msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.SCORE_HIGH.getPath());
        if (s >= 0.45) return msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.SCORE_AVERAGE.getPath());
        if (s >= 0.25) return msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.SCORE_LOW.getPath());
        return msg.getMessageNoPrefix(MessagesEnum.TESSERE.LOADOUT.SCORE_CHAOTIC.getPath());
    }

    // ── Résolution des compétences ─────────────────────────────────────────────

    private List<TessereSkill> resoudreCompetences(List<TessereType> eq) {
        Map<Domaines, Long> freq = eq.stream()
                .collect(Collectors.groupingBy(TessereType::getDomaine, Collectors.counting()));

        List<TessereSkill> res = new ArrayList<>();
        Set<TessereSkill> vus = new HashSet<>();

        check(res, vus, eq.contains(TessereType.ASCENSION),                                           TessereSkill.EVEIL_ARCANIQUE);
        check(res, vus, eq.contains(TessereType.OCCULTATION) && eq.contains(TessereType.CINETIQUE),   TessereSkill.LAME_DU_VIDE);
        check(res, vus, eq.contains(TessereType.DIMENSION)   && nb(freq, Domaines.SPACE) >= 2,        TessereSkill.CHOC_DIMENSIONNEL);
        check(res, vus, eq.contains(TessereType.TRANSLATION),                                          TessereSkill.BOND_INSTANTANE);
        check(res, vus, eq.contains(TessereType.INSTABILITE),                                          TessereSkill.CHAOS_LATENT);
        check(res, vus, eq.contains(TessereType.LUMINEUSE)   && nb(freq, Domaines.ENERGY) >= 2,       TessereSkill.BOUCLIER_LUMINE);
        check(res, vus, nb(freq, Domaines.ENERGY) >= 2       && eq.contains(TessereType.THERMIQUE),   TessereSkill.MARQUE_ARDENTE);
        check(res, vus, nb(freq, Domaines.ENERGY) >= 2       && eq.contains(TessereType.CINETIQUE),   TessereSkill.IMPACT_CINETIQUE);
        check(res, vus, nb(freq, Domaines.MATTER) >= 2,                                                TessereSkill.PEAU_DE_PIERRE);
        check(res, vus, nb(freq, Domaines.LIFE)   >= 2       && eq.contains(TessereType.REGENERATION),TessereSkill.REGENERATION_HP);
        check(res, vus, nb(freq, Domaines.LIFE)   >= 2,                                                TessereSkill.VITALITE_ELARGIE);
        check(res, vus, nb(freq, Domaines.AWARENESS) >= 2,                                             TessereSkill.VUE_ARCANIQUE);
        check(res, vus, nb(freq, Domaines.HARMONY) >= 2,                                               TessereSkill.HARMONIE_INT);
        check(res, vus, nb(freq, Domaines.TIME)   >= 2       && eq.contains(TessereType.ACCELERATION),TessereSkill.REFLEXES_ACCRUS);
        check(res, vus, nb(freq, Domaines.TIME)   >= 2       && eq.contains(TessereType.DUREE),       TessereSkill.DUREE_ETENDUE);
        check(res, vus, eq.contains(TessereType.THERMIQUE),                                            TessereSkill.MARQUE_ARDENTE);
        check(res, vus, eq.contains(TessereType.CINETIQUE),                                            TessereSkill.IMPACT_CINETIQUE);
        check(res, vus, eq.contains(TessereType.EXTINCTION),                                           TessereSkill.DECLIN_FORCE);
        check(res, vus, nb(freq, Domaines.MATTER) >= 1,                                                TessereSkill.ANCRAGE);
        check(res, vus, nb(freq, Domaines.LIFE)   >= 1,                                                TessereSkill.VITALITE_ELARGIE);
        check(res, vus, nb(freq, Domaines.AWARENESS) >= 1,                                             TessereSkill.INSTINCT_AFFINE);
        check(res, vus, eq.contains(TessereType.CROISSANCE),                                           TessereSkill.CROISSANCE_ACCL);
        check(res, vus, nb(freq, Domaines.HARMONY) >= 1,                                               TessereSkill.HARMONIE_INT);

        TessereSkill[] fallbacks = {TessereSkill.FLUX_I, TessereSkill.FLUX_II, TessereSkill.FLUX_III};
        for (int i = 0; res.size() < 3; i++) res.add(fallbacks[i]);
        return res;
    }

    private void check(List<TessereSkill> res, Set<TessereSkill> vus, boolean cond, TessereSkill skill) {
        if (res.size() < 3 && cond && !vus.contains(skill)) { res.add(skill); vus.add(skill); }
    }

    private long nb(Map<Domaines, Long> freq, Domaines d) {
        return freq.getOrDefault(d, 0L);
    }

    // ── Effets passifs ─────────────────────────────────────────────────────────

    public void appliquerEffetsPassifs(RPGPlayer rpg) {
        TessereLoadout loadout = rpg.getTessereLoadout();
        if (!loadout.isRevealed()) return;
        double score = loadout.getEfficaciteScore();

        for (Skill skill : loadout.getActiveSkills()) {
            if (!(skill instanceof TessereSkill ts)) continue;
            if (ts.getType() != SkillType.PASSIVE) continue;

            switch (ts) {
                case PEAU_DE_PIERRE   -> rpg.setArmor(rpg.getArmor() + (float) ts.param("bonus_armure", score));
                case ANCRAGE          -> rpg.setKnockbackResistance(rpg.getKnockbackResistance() + (float) ts.param("bonus_kb", score));
                case REFLEXES_ACCRUS  -> rpg.setAttackSpeed(rpg.getAttackSpeed() + (float) ts.param("bonus_attaque", score));
                case INSTINCT_AFFINE  -> rpg.setCritChance(rpg.getCritChance() + (float) ts.param("bonus_crit", score));
                case VITALITE_ELARGIE -> rpg.setMaxHealth(rpg.getMaxHealth() + ts.param("bonus_hp", score));
                case CROISSANCE_ACCL  -> rpg.setExpBonus(rpg.getExpBonus() + ts.param("bonus_xp", score));
                case HARMONIE_INT -> {
                    double b = ts.param("bonus_stats", score);
                    rpg.setStrength(rpg.getStrength() + b);
                    rpg.setDexterity(rpg.getDexterity() + b);
                    rpg.setConstitution(rpg.getConstitution() + b);
                }
                case EVEIL_ARCANIQUE -> {
                    double pct = ts.param("pct_boost", score);
                    rpg.setStrength(rpg.getStrength() * (1 + pct));
                    rpg.setDexterity(rpg.getDexterity() * (1 + pct));
                    rpg.setConstitution(rpg.getConstitution() * (1 + pct));
                    rpg.setIntelligence(rpg.getIntelligence() * (1 + pct));
                }
                case VUE_ARCANIQUE -> {
                    if (rpg.getBukkitPlayer() != null && rpg.getBukkitPlayer().isOnline())
                        rpg.getBukkitPlayer().addPotionEffect(
                                new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false, false));
                }
                default -> {}
            }
        }
        context.getPlayerService().refreshAttributes(rpg);
    }

    private void retirerEffetsPassifs(RPGPlayer rpg) {
        if (!rpg.getTessereLoadout().isRevealed()) return;
        if (rpg.getBukkitPlayer() != null && rpg.getBukkitPlayer().isOnline())
            rpg.getBukkitPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
        context.getPlayerService().applyRaceStats(rpg);
    }
}
