package fr.cozyhouse.cozyHouseRPG.visual.math;

/**
 * Allocation-free mutable 3D vector used exclusively in particle generation loops.
 *
 * WHY MUTABLE:
 *   Particle loops generate thousands of temporary coordinates per second.
 *   Using immutable objects would create enormous GC pressure → TPS drops.
 *   This class is used as a scratch variable: set() → transform() → use() → done.
 *
 * THREAD SAFETY: NOT thread-safe by design — particle rendering always runs on
 * the main server thread (Bukkit scheduler), so no synchronisation needed.
 */
public final class Vec3 {

    public double x, y, z;

    public Vec3()                            { this.x = 0; this.y = 0; this.z = 0; }
    public Vec3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }

    /** Set all components. Returns this for chaining. */
    public Vec3 set(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
        return this;
    }

    /** Copy from another vector. */
    public Vec3 set(Vec3 o) {
        this.x = o.x; this.y = o.y; this.z = o.z;
        return this;
    }

    /** Add another vector in-place. */
    public Vec3 add(double x, double y, double z) {
        this.x += x; this.y += y; this.z += z;
        return this;
    }

    /** Multiply all components in-place. */
    public Vec3 scale(double s) {
        this.x *= s; this.y *= s; this.z *= s;
        return this;
    }

    // ── Rotation operations ───────────────────────────────────────────────────

    /**
     * Rotate around the Y axis (horizontal rotation, for player yaw).
     *
     * Matrix:
     *  [  cosθ  0  sinθ ] [x]
     *  [  0     1  0    ] [y]
     *  [ -sinθ  0  cosθ ] [z]
     *
     * @param cosYaw  cos(yaw) — pre-computed, not recomputed per point
     * @param sinYaw  sin(yaw)
     */
    public Vec3 rotateY(double cosYaw, double sinYaw) {
        double nx = x * cosYaw + z * sinYaw;
        double nz = -x * sinYaw + z * cosYaw;
        x = nx;
        z = nz;
        return this;
    }

    /**
     * Rotate around the X axis (vertical tilt, for pitch and wing flap).
     *
     * Matrix:
     *  [ 1   0      0   ] [x]
     *  [ 0  cosθ  -sinθ ] [y]
     *  [ 0  sinθ   cosθ ] [z]
     */
    public Vec3 rotateX(double cosAngle, double sinAngle) {
        double ny = y * cosAngle - z * sinAngle;
        double nz = y * sinAngle + z * cosAngle;
        y = ny;
        z = nz;
        return this;
    }

    /**
     * Rotate around the Z axis (roll — used for wing droop).
     *
     * Matrix:
     *  [ cosθ  -sinθ  0 ] [x]
     *  [ sinθ   cosθ  0 ] [y]
     *  [  0      0    1 ] [z]
     */
    public Vec3 rotateZ(double cosAngle, double sinAngle) {
        double nx = x * cosAngle - y * sinAngle;
        double ny = x * sinAngle + y * cosAngle;
        x = nx;
        y = ny;
        return this;
    }

    /**
     * Full 3D rotation using Euler angles applied in Y-X-Z order.
     * Used to transform wing-local coordinates to world space.
     *
     * @param cosYaw   cos(playerYaw)
     * @param sinYaw   sin(playerYaw)
     * @param cosPitch cos(flapAngle)
     * @param sinPitch sin(flapAngle)
     */
    public Vec3 rotateYX(double cosYaw, double sinYaw, double cosPitch, double sinPitch) {
        return rotateX(cosPitch, sinPitch).rotateY(cosYaw, sinYaw);
    }

    /** Linear interpolation: this = lerp(a, b, t). */
    public static Vec3 lerp(Vec3 a, Vec3 b, double t, Vec3 out) {
        out.x = a.x + (b.x - a.x) * t;
        out.y = a.y + (b.y - a.y) * t;
        out.z = a.z + (b.z - a.z) * t;
        return out;
    }

    /** Length squared (avoids sqrt when only comparing distances). */
    public double lengthSq() {
        return x * x + y * y + z * z;
    }

    /** Euclidean length. */
    public double length() {
        return Math.sqrt(lengthSq());
    }

    @Override
    public String toString() {
        return String.format("Vec3(%.3f, %.3f, %.3f)", x, y, z);
    }
}
