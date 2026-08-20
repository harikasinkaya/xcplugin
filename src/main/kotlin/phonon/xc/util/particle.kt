package phonon.xc.util.particle

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class ParticlePacket(
    val particle: Particle = Particle.FLAME,
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
    val offsetX: Float = 0.1f,
    val offsetY: Float = 0.1f,
    val offsetZ: Float = 0.1f,
    val extra: Float = 1.0f,
    val count: Int = 10
) {
    fun spawn(location: Location) {
        location.world?.spawnParticle(
            particle,
            location.x + x,
            location.y + y,
            location.z + z,
            count,
            offsetX.toDouble(),
            offsetY.toDouble(),
            offsetZ.toDouble(),
            extra.toDouble()
        )
    }

    fun spawn(world: World, x: Double, y: Double, z: Double) {
        world.spawnParticle(
            particle,
            x + this.x,
            y + this.y,
            z + this.z,
            count,
            offsetX.toDouble(),
            offsetY.toDouble(),
            offsetZ.toDouble(),
            extra.toDouble()
        )
    }
}

fun spawnCircleParticles(
    location: Location,
    particle: Particle = Particle.FLAME,
    radius: Double = 1.0,
    count: Int = 20,
    yOffset: Double = 0.0
) {
    val world = location.world ?: return
    for (i in 0 until count) {
        val angle = (2 * PI * i) / count
        val x = location.x + cos(angle) * radius
        val z = location.z + sin(angle) * radius
        world.spawnParticle(particle, x, location.y + yOffset, z, 1, 0.0, 0.0, 0.0, 0.0)
    }
}

fun spawnSphereParticles(
    location: Location,
    particle: Particle = Particle.FLAME,
    radius: Double = 1.0,
    count: Int = 50
) {
    val world = location.world ?: return
    for (i in 0 until count) {
        val theta = (2 * PI * i) / count
        val phi = Math.acos(1 - 2 * ((i + 0.5) / count))
        val x = location.x + radius * sin(phi) * cos(theta)
        val y = location.y + radius * cos(phi)
        val z = location.z + radius * sin(phi) * sin(theta)
        world.spawnParticle(particle, x, y, z, 1, 0.0, 0.0, 0.0, 0.0)
    }
}
