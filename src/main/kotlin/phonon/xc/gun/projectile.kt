package phonon.xc.gun

import org.bukkit.Location
import org.bukkit.entity.Entity
import phonon.xc.util.hitbox.Hitbox
import java.util.UUID
import java.util.concurrent.*
import kotlin.math.floor
import kotlin.math.sqrt

data class Projectile(
    val id: UUID = UUID.randomUUID(),
    var x: Double,
    var y: Double,
    var z: Double,
    var vx: Double,
    var vy: Double,
    var vz: Double,
    val gravity: Double = 0.0,
    val lifetime: Int = 100,
    val range: Double = 100.0,
    val sourcePlayer: UUID,
    val gun: Gun,
    var ticksAlive: Int = 0,
    var distanceTraveled: Double = 0.0
)

data class RaytraceResult(
    val hitEntity: Entity? = null,
    val hitLocation: Location? = null,
    val isHeadshot: Boolean = false,
    val hitBlock: Boolean = false
)

data class ChunkCoord3D(val x: Int, val y: Int, val z: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChunkCoord3D) return false
        return x == other.x && y == other.y && z == other.z
    }
    override fun hashCode(): Int = 31 * (31 * (31 + x) + y) + z
}

class ProjectileSystem(private val numThreads: Int) {
    private val executor: ExecutorService = Executors.newFixedThreadPool(numThreads)
    private val projectileQueue: ConcurrentLinkedQueue<Projectile> = ConcurrentLinkedQueue()
    private val hitResults: ConcurrentHashMap<UUID, RaytraceResult> = ConcurrentHashMap()

    fun addProjectile(p: Projectile) {
        projectileQueue.offer(p)
    }

    fun update(hitboxes: HashMap<ChunkCoord3D, List<Hitbox>>): List<Pair<Projectile, RaytraceResult>> {
        val projectiles = mutableListOf<Projectile>()
        while (projectileQueue.isNotEmpty()) {
            projectileQueue.poll()?.let { projectiles.add(it) }
        }
        if (projectiles.isEmpty()) return emptyList()

        val chunkSize = (projectiles.size + numThreads - 1) / numThreads
        val futures = mutableListOf<Future<List<Pair<Projectile, RaytraceResult>>>>()

        for (i in 0 until numThreads) {
            val start = i * chunkSize
            val end = minOf(start + chunkSize, projectiles.size)
            if (start >= projectiles.size) break
            
            val subset = projectiles.subList(start, end)
            futures.add(executor.submit(Callable {
                subset.mapNotNull { p ->
                    val result = runProjectileRaytrace(p, hitboxes)
                    if (result != null) p to result else null
                }
            }))
        }

        val results = mutableListOf<Pair<Projectile, RaytraceResult>>()
        for (future in futures) {
            try {
                results.addAll(future.get(5000, TimeUnit.MILLISECONDS))
            } catch (e: Exception) {
                // Handle timeout or error
            }
        }
        return results
    }

    fun shutdown() {
        executor.shutdown()
    }
}

fun runProjectileRaytrace(p: Projectile, hitboxes: HashMap<ChunkCoord3D, List<Hitbox>>): RaytraceResult? {
    val prevX = p.x - p.vx
    val prevY = p.y - p.vy
    val prevZ = p.z - p.vz
    
    val dx = p.vx
    val dy = p.vy
    val dz = p.vz

    var ix = floor(prevX).toInt()
    var iy = floor(prevY).toInt()
    var iz = floor(prevZ).toInt()
    
    val endX = floor(p.x).toInt()
    val endY = floor(p.y).toInt()
    val endZ = floor(p.z).toInt()

    val stepX = if (dx > 0) 1 else if (dx < 0) -1 else 0
    val stepY = if (dy > 0) 1 else if (dy < 0) -1 else 0
    val stepZ = if (dz > 0) 1 else if (dz < 0) -1 else 0

    val tDeltaX = if (dx != 0.0) abs(1.0 / dx) else Double.MAX_VALUE
    val tDeltaY = if (dy != 0.0) abs(1.0 / dy) else Double.MAX_VALUE
    val tDeltaZ = if (dz != 0.0) abs(1.0 / dz) else Double.MAX_VALUE

    var tMaxX = if (dx != 0.0) ((if (stepX > 0) (ix + 1) else ix) - prevX) / dx else Double.MAX_VALUE
    var tMaxY = if (dy != 0.0) ((if (stepY > 0) (iy + 1) else iy) - prevY) / dy else Double.MAX_VALUE
    var tMaxZ = if (dz != 0.0) ((if (stepZ > 0) (iz + 1) else iz) - prevZ) / dz else Double.MAX_VALUE

    while (true) {
        // Check entity hitboxes in current voxel
        val coord = ChunkCoord3D(ix, iy, iz)
        hitboxes[coord]?.forEach { hitbox ->
            if (hitbox.intersectsRay(prevX, prevY, prevZ, dx, dy, dz)) {
                val hitY = prevY + (hitbox.minY - prevY) / dy * dy
                return RaytraceResult(
                    hitEntity = hitbox.entity,
                    hitLocation = Location(hitbox.entity.world, p.x, p.y, p.z),
                    isHeadshot = hitbox.isHeadshot(hitY),
                    hitBlock = false
                )
            }
        }

        if (ix == endX && iy == endY && iz == endZ) break

        // Move to next voxel
        when {
            tMaxX < tMaxY && tMaxX < tMaxZ -> {
                ix += stepX
                tMaxX += tDeltaX
            }
            tMaxY < tMaxZ -> {
                iy += stepY
                tMaxY += tDeltaY
            }
            else -> {
                iz += stepZ
                tMaxZ += tDeltaZ
            }
        }
    }

    return null
}

private fun abs(x: Double) = kotlin.math.abs(x)
