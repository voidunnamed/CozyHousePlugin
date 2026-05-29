package fr.cozyhouse.cozyHouseRPG.visual.effects;

import fr.cozyhouse.cozyHouseRPG.visual.math.TrigCache;
import fr.cozyhouse.cozyHouseRPG.visual.math.Vec3;
import fr.cozyhouse.cozyHouseRPG.visual.nms.ParticlePacketSender;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Generates a pair of feathered angel wings using a mathematical multi-layer
 * parametric model.  Every feather is individually animated.
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 *  COORDINATE SYSTEM (player-local space, origin = player's feet)
 * ───────────────────────────────────────────────────────────────────────────────
 *  +X  =  player's right       (world-relative after yaw rotation)
 *  +Y  =  up
 *  +Z  =  behind the player    (back)
 *
 *  Transformation to world:
 *    worldX = px  +  lx * (-cosYaw)  +  lz * sinYaw
 *    worldY = py  +  ly
 *    worldZ = pz  +  lx * (-sinYaw)  +  lz * (-cosYaw)
 *
 *  where yawRad = toRadians( player.getYaw() )
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 *  WING ANATOMY  (built for each half, left = mirror of right)
 * ───────────────────────────────────────────────────────────────────────────────
 *  Shoulder joint  (S)  : attachment point at upper back, y ≈ 1.25
 *  Leading edge    (L)  : curved arc from S to wingtip, drawn with END_ROD glow
 *  Primary feathers     : 8 long feathers at the distal (outer) third
 *  Secondary feathers   : 11 medium feathers along the inner two thirds
 *  Covert feathers      : 5 short feathers at the wing root (near shoulder)
 *  Membrane shimmer     : sparse SOUL particles between secondaries
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 *  BEAT ANIMATION
 * ───────────────────────────────────────────────────────────────────────────────
 *  Frequency  : 0.55 Hz  (slow, majestic, bird-of-paradise cadence)
 *  Phase      : each feather adds a small phase offset → realistic wave ripple
 *  Asymmetry  : downstroke faster (power stroke), upstroke slower (recovery)
 *  Droop      : tip droops slightly during downstroke (air resistance effect)
 *  Twist      : tip lags the root by ~30° during the beat (aeroelastic flex)
 */
public final class AngelWingsEffect {

    // ── Wing geometry constants ────────────────────────────────────────────────

    /** Total half-span (shoulder to tip), in blocks. */
    private static final double SPAN           = 2.6;

    /** Maximum arch height at mid-span (wings curve upward toward center). */
    private static final double ARCH_HEIGHT    = 0.55;

    /** Tip sweep-back (tip trails behind shoulder). Quadratic increase toward tip. */
    private static final double SWEEP_BACK     = 0.30;

    /** Wing chord at root (perpendicular depth of wing cross-section). */
    private static final double CHORD_ROOT     = 0.75;

    /** Taper exponent: 1.0 = linear taper, 0.5 = broad mid-section with sharp tip. */
    private static final double CHORD_TAPER    = 0.55;

    /**
     * Fraction by which primary feather tips extend beyond the leading edge.
     * >1 means they project forward of the leading edge (realistic for splayed primaries).
     */
    private static final double PRIM_EXTEND    = 0.45;
    private static final double SEC_EXTEND     = 0.15;
    private static final double COVERT_EXTEND  = 0.05;

    // ── Shoulder attachment offsets (player-local) ─────────────────────────────

    private static final double SH_X  = 0.28;   // outward from spine
    private static final double SH_Y  = 1.25;   // height above feet
    private static final double SH_Z  = 0.12;   // behind player (into +Z local)

    // ── Feather counts ────────────────────────────────────────────────────────

    /** Primary feathers (distal wing, u ∈ [0.55, 1.0]). */
    private static final int N_PRIMARY   = 8;
    /** Secondary feathers (inner wing, u ∈ [0.12, 0.55]). */
    private static final int N_SECONDARY = 11;
    /** Covert feathers (root, u ∈ [0.0, 0.12]). */
    private static final int N_COVERT    = 5;

    // ── Beat animation parameters ─────────────────────────────────────────────

    /** Beat frequency in rad/tick (server runs at 20 TPS, we call every 2 ticks). */
    private static final double BEAT_FREQ        = 2.0 * Math.PI * 0.55 / 10.0; // 0.55 Hz at 10 FPS
    private static final double BEAT_AMP_PRIMARY  = 0.52;   // max vertical displacement at tip
    private static final double BEAT_AMP_ROOT     = 0.08;   // displacement at shoulder
    /** Phase ripple added per unit of span — feather tips lag the root. */
    private static final double PHASE_RIPPLE_U    = -0.55;
    /** Downstroke asymmetry: shape exponent for the breathing oscillator. */
    private static final double BEAT_SHAPE        = 0.68;

    // ── Particle sizes ────────────────────────────────────────────────────────

    private static final float SZ_PRIMARY   = 0.42f;
    private static final float SZ_SECONDARY = 0.38f;
    private static final float SZ_COVERT    = 0.30f;
    private static final float SZ_LEADING   = 0.25f;
    private static final float SZ_SHADOW    = 0.35f;

    // ── Colors ────────────────────────────────────────────────────────────────
    // All as (r,g,b) float[3] to avoid Color object allocations in the hot loop.

    private static final float[] COL_WHITE        = {1.0f, 1.0f, 1.0f};
    private static final float[] COL_CREAM        = {1.0f, 0.97f, 0.92f};
    private static final float[] COL_SHADOW       = {0.82f, 0.82f, 0.90f};
    private static final float[] COL_PRIM_TIP     = {0.98f, 0.98f, 1.0f};

    // Pre-built Bukkit Color for fallback path (one allocation at class load, reused)
    private static final Particle.DustOptions DUST_WHITE =
        new Particle.DustOptions(Color.fromRGB(255, 255, 255), SZ_PRIMARY);
    private static final Particle.DustOptions DUST_CREAM =
        new Particle.DustOptions(Color.fromRGB(255, 247, 235), SZ_SECONDARY);
    private static final Particle.DustOptions DUST_SHADOW =
        new Particle.DustOptions(Color.fromRGB(209, 209, 230), SZ_SHADOW);
    private static final Particle.DustOptions DUST_LEADING =
        new Particle.DustOptions(Color.fromRGB(255, 255, 255), SZ_LEADING);

    // ── Scratch vectors (reused, no allocation) ────────────────────────────────

    private final Vec3 leading  = new Vec3();
    private final Vec3 trailing = new Vec3();
    private final Vec3 featherTip = new Vec3();
    private final Vec3 ctrl     = new Vec3();
    private final Vec3 pt       = new Vec3();

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Renders one frame of the angel wings for {@code subject}, sending particles
     * to all {@code viewers} who are close enough.
     *
     * @param subject  the admin player wearing the wings
     * @param viewers  all players who should receive the particles (pre-culled)
     * @param tick     absolute tick counter (drives animation)
     */
    public void render(Player subject, Collection<Player> viewers, long tick) {
        if (viewers.isEmpty()) return;

        Location loc = subject.getLocation();
        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        double yawRad = Math.toRadians(loc.getYaw());
        double cosYaw = TrigCache.cos(yawRad);
        double sinYaw = TrigCache.sin(yawRad);

        // Beat phase (time drives the oscillation)
        double t          = tick * BEAT_FREQ;
        double rawBeat    = TrigCache.breathe(t, 1.0, BEAT_SHAPE);
        // Bias upward: wings spend more time raised than lowered (natural flight)
        double beatBiased = (rawBeat + 0.25) / 1.25;

        // Right wing  (side = +1)
        renderWing(viewers, px, py, pz, cosYaw, sinYaw, +1.0, beatBiased, t);
        // Left wing   (side = -1, mirror)
        renderWing(viewers, px, py, pz, cosYaw, sinYaw, -1.0, beatBiased, t);
    }

    // ── Wing rendering ────────────────────────────────────────────────────────

    private void renderWing(Collection<Player> viewers,
                            double px, double py, double pz,
                            double cosYaw, double sinYaw,
                            double side,          // +1 = right, -1 = left
                            double beatBiased,    // normalized beat value [0, 1]
                            double t) {

        // ── Leading edge pass ──────────────────────────────────────────────────
        // Dense glowing particles trace the curved leading edge.
        // 24 evenly spaced points along u ∈ [0, 1].

        int N_LEAD = 22;
        for (int li = 0; li <= N_LEAD; li++) {
            double u = (double) li / N_LEAD;
            computeLeadingPoint(u, side, beatBiased, t, leading);
            // Local → World
            double wx = px + leading.x * (-cosYaw) + leading.z * sinYaw;
            double wy = py + leading.y;
            double wz = pz + leading.x * (-sinYaw) + leading.z * (-cosYaw);
            for (Player v : viewers) {
                // Leading edge: END_ROD for glow at every 3rd point, DUST elsewhere
                if (li % 3 == 0) {
                    ParticlePacketSender.sendGlow(v, wx, wy, wz);
                } else {
                    ParticlePacketSender.sendDust(v, wx, wy, wz,
                        COL_WHITE[0], COL_WHITE[1], COL_WHITE[2], SZ_LEADING);
                }
            }
        }

        // ── Primary feathers (8, distal wing) ─────────────────────────────────
        for (int i = 0; i < N_PRIMARY; i++) {
            double u = 0.555 + (0.445 * i) / (N_PRIMARY - 1.0);
            renderFeather(viewers, px, py, pz, cosYaw, sinYaw, side, beatBiased, t,
                u, PRIM_EXTEND, 10, COL_WHITE, COL_PRIM_TIP, SZ_PRIMARY,
                /* addGlowTip= */ (i == N_PRIMARY - 1));
        }

        // ── Secondary feathers (11, inner wing) ───────────────────────────────
        for (int i = 0; i < N_SECONDARY; i++) {
            double u = 0.12 + (0.43 * i) / (N_SECONDARY - 1.0);
            renderFeather(viewers, px, py, pz, cosYaw, sinYaw, side, beatBiased, t,
                u, SEC_EXTEND, 7, COL_CREAM, COL_WHITE, SZ_SECONDARY,
                /* addGlowTip= */ false);
        }

        // ── Covert feathers (5, root) ─────────────────────────────────────────
        for (int i = 0; i < N_COVERT; i++) {
            double u = (0.12 * i) / (N_COVERT - 1.0);
            renderFeather(viewers, px, py, pz, cosYaw, sinYaw, side, beatBiased, t,
                u, COVERT_EXTEND, 5, COL_SHADOW, COL_CREAM, SZ_COVERT,
                /* addGlowTip= */ false);
        }

        // ── Membrane shimmer (sparse soul particles between secondaries) ───────
        renderMembrane(viewers, px, py, pz, cosYaw, sinYaw, side, beatBiased, t);
    }

    // ── Single feather ────────────────────────────────────────────────────────

    /**
     * Renders one feather as a quadratic Bézier curve from its trailing root
     * to its tip, which extends {@code extendFraction} beyond the leading edge.
     *
     *   B(s) = (1-s)²·P0  +  2(1-s)s·P1  +  s²·P2
     *
     * @param u             position along span [0..1]
     * @param extendFraction fraction by which tip extends beyond leading edge
     * @param steps         number of particles along this feather
     * @param rootColor     colour at feather root (float[3] r,g,b)
     * @param tipColor      colour at feather tip  (float[3] r,g,b)
     * @param size          DustOptions particle size
     * @param addGlowTip    if true, spawns an END_ROD at the very tip
     */
    private void renderFeather(Collection<Player> viewers,
                               double px, double py, double pz,
                               double cosYaw, double sinYaw,
                               double side, double beatBiased, double t,
                               double u, double extendFraction,
                               int steps,
                               float[] rootColor, float[] tipColor, float size,
                               boolean addGlowTip) {

        // Leading and trailing edge points at this span position
        computeLeadingPoint(u, side, beatBiased, t, leading);
        computeTrailingPoint(u, side, beatBiased, t, leading, trailing);

        // Feather direction: from trailing toward and beyond leading
        double fdx = leading.x - trailing.x;
        double fdy = leading.y - trailing.y;
        double fdz = leading.z - trailing.z;
        double flen = Math.sqrt(fdx * fdx + fdy * fdy + fdz * fdz);
        if (flen < 1e-6) return;
        double inv = (1.0 + extendFraction) / flen;
        fdx *= inv; fdy *= inv; fdz *= inv;

        // Feather tip = trailing + extended direction
        featherTip.set(trailing.x + fdx, trailing.y + fdy, trailing.z + fdz);

        // Bézier control point: midpoint + slight outward bow (feather vane curvature)
        double midX = (trailing.x + featherTip.x) * 0.5;
        double midY = (trailing.y + featherTip.y) * 0.5;
        double midZ = (trailing.z + featherTip.z) * 0.5;
        // Bow outward in the direction perpendicular to feather and up-axis:
        // cross(feather_dir, up) gives the in-plane perpendicular
        double perpX = fdz;   // cross(feather_dir, (0,1,0)) simplified: (dz, 0, -dx)
        double perpZ = -fdx;
        double perpLen = Math.sqrt(perpX * perpX + perpZ * perpZ);
        if (perpLen > 1e-6) {
            double bow = 0.035 * side;   // slight outward bow, mirrored per wing
            midX += perpX / perpLen * bow;
            midZ += perpZ / perpLen * bow;
        }
        // Slight droop during downstroke (air resistance visual cue)
        midY -= 0.018 * Math.max(0.0, -beatBiased * 2.0 + 1.0);
        ctrl.set(midX, midY, midZ);

        // Draw the Bézier curve: B(s) = (1-s)²·P0 + 2(1-s)s·P1 + s²·P2
        for (int si = 0; si <= steps; si++) {
            double s  = (double) si / steps;
            double s2 = s * s;
            double s1 = 1.0 - s;
            double s12 = s1 * s1;
            double c0 = s12;
            double c1 = 2.0 * s1 * s;
            double c2 = s2;

            pt.x = c0 * trailing.x + c1 * ctrl.x + c2 * featherTip.x;
            pt.y = c0 * trailing.y + c1 * ctrl.y + c2 * featherTip.y;
            pt.z = c0 * trailing.z + c1 * ctrl.z + c2 * featherTip.z;

            // Local → World
            double wx = px + pt.x * (-cosYaw) + pt.z * sinYaw;
            double wy = py + pt.y;
            double wz = pz + pt.x * (-sinYaw) + pt.z * (-cosYaw);

            // Interpolate colour root→tip
            float ir = rootColor[0] + (tipColor[0] - rootColor[0]) * (float) s;
            float ig = rootColor[1] + (tipColor[1] - rootColor[1]) * (float) s;
            float ib = rootColor[2] + (tipColor[2] - rootColor[2]) * (float) s;

            for (Player v : viewers) {
                ParticlePacketSender.sendDust(v, wx, wy, wz, ir, ig, ib, size);
            }
        }

        // Glowing tip on the outermost primary feather
        if (addGlowTip) {
            double wx = px + featherTip.x * (-cosYaw) + featherTip.z * sinYaw;
            double wy = py + featherTip.y;
            double wz = pz + featherTip.x * (-sinYaw) + featherTip.z * (-cosYaw);
            for (Player v : viewers) {
                ParticlePacketSender.sendGlow(v, wx, wy, wz);
            }
        }
    }

    // ── Membrane shimmer ───────────────────────────────────────────────────────

    /**
     * Scatters a handful of SOUL particles between secondary feathers to suggest
     * a translucent membrane (birds have a thin skin stretched between feather shafts).
     * Very sparse — only 6 particles per wing to stay imperceptible on its own,
     * but cumulatively creates depth.
     */
    private void renderMembrane(Collection<Player> viewers,
                                double px, double py, double pz,
                                double cosYaw, double sinYaw,
                                double side, double beatBiased, double t) {

        int N_SOUL = 7;
        for (int i = 0; i < N_SOUL; i++) {
            // Random-looking but deterministic position (function of i only)
            double u = 0.18 + (0.35 * i) / N_SOUL;
            double v = 0.35 + 0.30 * TrigCache.sin(i * 1.37 + t * 0.4);

            computeLeadingPoint(u, side, beatBiased, t, leading);
            computeTrailingPoint(u, side, beatBiased, t, leading, trailing);

            // Interpolate between trailing and leading
            double ix = trailing.x + (leading.x - trailing.x) * v;
            double iy = trailing.y + (leading.y - trailing.y) * v;
            double iz = trailing.z + (leading.z - trailing.z) * v;

            double wx = px + ix * (-cosYaw) + iz * sinYaw;
            double wy = py + iy;
            double wz = pz + ix * (-sinYaw) + iz * (-cosYaw);

            for (Player viewer : viewers) {
                ParticlePacketSender.sendSoul(viewer, wx, wy, wz);
            }
        }
    }

    // ── Parametric wing geometry ──────────────────────────────────────────────

    /**
     * Computes the leading-edge position at span parameter {@code u}.
     *
     *   x(u) = side * [ shoulder_X + u * SPAN * (1 - 0.08*u) ]
     *              ↑ outward extension, slight inward taper near tip
     *
     *   y(u) = shoulder_Y
     *          + ARCH_HEIGHT * sin(π*u)              ← arch (peak at mid-span)
     *          + beatDisp(u, beatBiased, t)           ← animated beat displacement
     *
     *   z(u) = shoulder_Z + SWEEP_BACK * u²           ← tip sweeps backward
     *
     * Beat displacement with phase ripple (tip lags root → realistic flex):
     *   disp(u) = lerp(BEAT_AMP_ROOT, BEAT_AMP_PRIMARY, u)
     *             * (beatBiased * 2 - 1)
     *             * (1 + phaseShift)
     *
     * where phaseShift accounts for wing aeroelastic twist at the tip.
     *
     * @param out  scratch Vec3 set to the result
     */
    private void computeLeadingPoint(double u, double side, double beatBiased,
                                     double t, Vec3 out) {
        // Span: outward taper (tip very slightly narrower in x)
        double spanX = SPAN * u * (1.0 - 0.08 * u);

        // Arch: wings curve upward then droop at tip
        double arch = ARCH_HEIGHT * TrigCache.sin(Math.PI * u)
                    + 0.08 * TrigCache.sin(Math.PI * u * u);   // secondary lobe near tip

        // Beat: the flap displacement, with phase ripple (tip lags root)
        double phaseShift = PHASE_RIPPLE_U * u;  // negative → tip leads by phase
        double beatPhaseAtU = t + phaseShift;
        double beatLocalRaw = TrigCache.breathe(beatPhaseAtU, 1.0, BEAT_SHAPE);
        double beatLocal    = (beatLocalRaw + 0.25) / 1.25;  // same bias as outer beat
        double beatAmp  = TrigCache.lerp(BEAT_AMP_ROOT, BEAT_AMP_PRIMARY, u);
        double beatDisp = beatAmp * (beatLocal * 2.0 - 1.0);  // remap [0,1]→[-1,+1]

        // Tip droop during downstroke (beatBiased < 0.5 → going down)
        double droop = 0.07 * Math.max(0.0, 1.0 - beatBiased * 2.5) * u * u;

        out.set(
            side * (SH_X + spanX),
            SH_Y + arch + beatDisp - droop,
            SH_Z + SWEEP_BACK * u * u
        );
    }

    /**
     * Computes the trailing-edge position based on the already-computed leading point.
     *
     * The trailing edge is the "rear" of the wing cross-section.
     * It sits below and slightly behind the leading edge.
     *
     * chord(u) = CHORD_ROOT * (1 - u)^CHORD_TAPER   [tapers to 0 at tip]
     *
     * @param leadingPt already computed leading edge point (not modified)
     * @param out       result written here
     */
    private void computeTrailingPoint(double u, double side, double beatBiased,
                                      double t, Vec3 leadingPt, Vec3 out) {
        double chord = CHORD_ROOT * Math.pow(Math.max(0.0, 1.0 - u), CHORD_TAPER);

        // Trailing edge is chord-length below the leading edge, slightly more behind
        out.set(
            leadingPt.x * 0.93,          // slight inward lean
            leadingPt.y - chord,         // drop by chord length
            leadingPt.z + 0.18 * (1.0 - u)  // more behind near root, aligns at tip
        );
    }

    // ── LOD helper ────────────────────────────────────────────────────────────

    /**
     * Returns a detail multiplier [0.0, 1.0] based on the squared distance
     * between the viewer and the subject.  Used by VisualEffectManager to
     * decide whether to call render() at all and to filter the viewer list.
     *
     * @param distSq distance² in blocks²
     */
    public static double detailLevel(double distSq) {
        if (distSq >  900) return 0.0;  // > 30 blocks → skip
        if (distSq >  256) return 0.4;  // 16–30 blocks → low detail
        if (distSq >   64) return 0.7;  // 8–16 blocks  → medium
        return                    1.0;  // < 8  blocks  → full
    }
}
