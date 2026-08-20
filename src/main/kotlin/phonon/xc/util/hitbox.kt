package phonon.xc.util.hitbox

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

data class Hitbox(
    val entity: Entity,
    val minX: Double,
    val minY: Double,
    val minZ: Double,
    val maxX: Double,
    val maxY: Double,
    val maxZ: Double,
    val headshotMinY: Double = 0.0
) {
    fun intersectsRay(ox: Double, oy: Double, oz: Double, dx: Double, dy: Double, dz: Double): Boolean {
        val tx1 = (minX - ox) / dx
        val tx2 = (maxX - ox) / dx
        var tmin = min(tx1, tx2)
        var tmax = max(tx1, tx2)

        val ty1 = (minY - oy) / dy
        val ty2 = (maxY - oy) / dy
        tmin = max(tmin, min(ty1, ty2))
        tmax = min(tmax, max(ty1, ty2))

        val tz1 = (minZ - oz) / dz
        val tz2 = (maxZ - oz) / dz
        tmin = max(tmin, min(tz1, tz2))
        tmax = min(tmax, max(tz1, tz2))

        return tmax >= 0 && tmin <= tmax && tmin <= 1.0
    }

    fun isHeadshot(hitY: Double): Boolean = hitY >= headshotMinY

    companion object {
        fun fromEntity(entity: Entity): Hitbox {
            val loc = entity.location
            val bb = entity.boundingBox
            val isPlayer = entity is Player
            
            return Hitbox(
                entity = entity,
                minX = bb.minX,
                minY = bb.minY,
                minZ = bb.minZ,
                maxX = bb.maxX,
                maxY = bb.maxY,
                maxZ = bb.maxZ,
                headshotMinY = if (isPlayer) loc.y + 1.6 else loc.y + (bb.height * 0.8)
            )
        }
    }
}

data class HitboxSize(val width: Double, val height: Double)

val DEFAULT_HITBOX_SIZES = mapOf<String, HitboxSize>(
    "player" to HitboxSize(0.6, 1.8),
    "zombie" to HitboxSize(0.6, 1.95),
    "skeleton" to HitboxSize(0.6, 1.99),
    "creeper" to HitboxSize(0.6, 1.7),
    "spider" to HitboxSize(1.4, 0.9),
    "enderman" to HitboxSize(0.6, 2.9),
).withDefault { HitboxSize(0.6, 1.8) }
