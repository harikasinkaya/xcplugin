package phonon.xc.util.explosion

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import phonon.xc.util.damage.calculateExplosionDamage
import phonon.xc.util.worldguard.canExplode
import kotlin.math.max
import kotlin.math.sqrt

data class ExplosionConfig(
    val baseDamage: Double = 10.0,
    val radius: Double = 5.0,
    val falloff: Double = 2.0,
    val blockDamage: Boolean = true,
    val fireChance: Float = 0.0f,
    val armorReduction: Double = 0.5,
    val blastProtReduction: Double = 0.3
)

fun createExplosion(
    location: Location,
    config: ExplosionConfig = ExplosionConfig(),
    sourcePlayer: Player? = null
): List<Entity> {
    if (!canExplode(location)) return emptyList()

    val world = location.world ?: return emptyList()
    val affectedEntities = mutableListOf<Entity>()
    val entities = world.getNearbyEntities(location, config.radius, config.radius, config.radius)

    for (entity in entities) {
        if (entity !is LivingEntity) continue
        if (entity is Player && sourcePlayer != null && entity.uniqueId == sourcePlayer.uniqueId) continue

        val distance = entity.location.distance(location)
        if (distance > config.radius) continue

        val armor = entity.equipment?.let { 
            it.boots.defensePoints + it.leggings.defensePoints + 
            it.chestplate.defensePoints + it.helmet.defensePoints 
        } ?: 0
        
        val blastProt = entity.equipment?.items?.sumOf { 
            it.enchantmentEnchants?.get(org.bukkit.enchantments.Enchantment.BLAST_PROTECTION) ?: 0 
        }?.toInt() ?: 0

        val damage = calculateExplosionDamage(
            baseDamage = config.baseDamage,
            distance = distance,
            radius = config.radius,
            falloff = config.falloff,
            armor = armor,
            blastProt = blastProt,
            armorRed = config.armorReduction,
            blastProtRed = config.blastProtReduction
        )

        if (damage > 0) {
            entity.damage(damage, sourcePlayer)
            affectedEntities.add(entity)
        }
    }

    // Block damage (optional)
    if (config.blockDamage) {
        // Vanilla explosion logic would go here with NMS
        // For now, just a simple version
    }

    return affectedEntities
}
