package fr.cozyhouse.cozyHouseRPG.skills.logic;

import fr.cozyhouse.cozyHouseRPG.skills.datas.Domaines;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class TessereCalcul {

    public enum Charge { POSITIVE, NEUTRAL, NEGATIVE }
    public enum Entropy { ORDER, BALANCE, CHAOS }
    public enum FLOW { ASCENDING, STABLE, DESCENDING }

    @AllArgsConstructor
    public enum Compatibility {
        ALFA(5),
        BETA(4),
        GAMMA(3),
        DELTA(2),
        EPSILON(1),
        ZETA(0);
        public final int number;
    }

    private final Charge charge;
    private final Entropy entropy;
    private final FLOW flow;
    private final Domaines domaines;

    public TessereCalcul(Charge charge, Entropy entropy, FLOW flow, Domaines domaines) {
        this.charge   = charge;
        this.entropy  = entropy;
        this.flow     = flow;
        this.domaines = domaines;
    }

    public static Compatibility calculResonance(TessereCalcul a, TessereCalcul b) {
        int score = 0;
        int oppositions = 0;

        // ── Charge ────────────────────────────────────────────────────────────
        if (a.charge == b.charge) {
            score += Compatibility.ALFA.number;
        } else if ((a.charge == Charge.NEUTRAL || b.charge == Charge.NEUTRAL)
                && (a.charge != Charge.NEGATIVE && b.charge != Charge.NEGATIVE)) {
            score += Compatibility.GAMMA.number;
        } else if (((a.charge == Charge.NEGATIVE) && (b.charge == Charge.POSITIVE))
                || ((b.charge == Charge.NEGATIVE) && (a.charge == Charge.POSITIVE))) {
            score += Compatibility.EPSILON.number;
        } else {
            oppositions++;
        }

        // ── Entropie ──────────────────────────────────────────────────────────
        if (a.entropy == b.entropy) {
            score += Compatibility.ALFA.number;
        } else if ((a.entropy == Entropy.BALANCE || b.entropy == Entropy.BALANCE)
                && (a.entropy != Entropy.CHAOS && b.entropy != Entropy.CHAOS)) {
            score += Compatibility.GAMMA.number;
        } else if (((a.entropy == Entropy.CHAOS) && (b.entropy == Entropy.ORDER))
                || ((b.entropy == Entropy.CHAOS) && (a.entropy == Entropy.ORDER))) {
            score += Compatibility.EPSILON.number;
        } else {
            oppositions++;
        }

        // ── Flux ──────────────────────────────────────────────────────────────
        if (a.flow == b.flow) {
            score += Compatibility.ALFA.number;
        } else if ((a.flow == FLOW.STABLE || b.flow == FLOW.STABLE)
                && (a.flow != FLOW.DESCENDING && b.flow != FLOW.DESCENDING)) {
            score += Compatibility.GAMMA.number;
        } else if (((a.flow == FLOW.DESCENDING) && (b.flow == FLOW.ASCENDING))
                || ((b.flow == FLOW.DESCENDING) && (a.flow == FLOW.ASCENDING))) {
            score += Compatibility.EPSILON.number;
        } else {
            oppositions++;
        }

        // ── Moyenne des 3 axes ────────────────────────────────────────────────
        score = Math.round(score / 3.0F);

        // ── Bonus de domaine partagé ──────────────────────────────────────────
        if (a.domaines == b.domaines) {
            score += 1;
        }

        // ── Pénalités d'opposition ────────────────────────────────────────────
        if ((oppositions == 1) && (score > Compatibility.EPSILON.number)) score = 1;
        if (oppositions >= 2) score = 0;

        // ── Correspondance au niveau de Compatibility ─────────────────────────
        for (Compatibility compatibility : Compatibility.values()) {
            if (compatibility.number == score) {
                return compatibility;
            }
        }
        return Compatibility.ZETA; // score inattendu → incompatible par défaut
    }
}
