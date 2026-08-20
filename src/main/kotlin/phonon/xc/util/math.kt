package phonon.xc.util

import kotlin.math.abs
import kotlin.random.Random

fun clamp(value: Double, min: Double, max: Double): Double {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun clamp(value: Float, min: Float, max: Float): Float {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun clamp(value: Int, min: Int, max: Int): Int {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun randomGaussian(mean: Double = 0.0, stdDev: Double = 1.0): Double {
    var u = 0.0
    var v = 0.0
    while (u == 0.0) u = Random.nextDouble()
    while (v == 0.0) v = Random.nextDouble()
    val num = abs(2.0 * u - 1.0)
    val denom = abs(2.0 * v - 1.0)
    val mult = num.coerceAtLeast(denom)
    if (mult == 0.0) return mean
    return mean + stdDev * (-2.0 * ln(mult) / mult).sqrt() * (if (Random.nextBoolean()) 1.0 else -1.0)
}

private fun ln(x: Double) = kotlin.math.ln(x)
private fun Double.sqrt() = kotlin.math.sqrt(this)

fun spreadDirection(baseDir: org.bukkit.util.Vector, spread: Float): org.bukkit.util.Vector {
    val yaw = kotlin.math.atan2(baseDir.z, baseDir.x)
    val pitch = kotlin.math.asin(clamp(baseDir.y, -1.0, 1.0))
    
    val yawOffset = (Random.nextDouble() - 0.5) * spread
    val pitchOffset = (Random.nextDouble() - 0.5) * spread
    
    val newYaw = yaw + yawOffset
    val newPitch = pitch + pitchOffset
    
    val cosPitch = kotlin.math.cos(newPitch)
    return org.bukkit.util.Vector(
        cosPitch * kotlin.math.cos(newYaw),
        kotlin.math.sin(newPitch),
        cosPitch * kotlin.math.sin(newYaw)
    ).normalize()
}
