package phonon.xc.util.damage

enum class DamageType {
    NORMAL,
    EXPLOSION,
    FIRE,
    WITHER,
    FALL,
    DROWN,
    VOID,
    CONTACT,
    PROJECTILE,
    THROWN,
    LANDMINE,
    BLEED,
    CUSTOM
}

fun calculateDamage(
    baseDamage: Double,
    armor: Int,
    resistance: Int,
    armorReduction: Double = 0.5,
    resistReduction: Double = 0.5,
): Double {
    val afterArmor = baseDamage - (armor * armorReduction)
    val afterResist = afterArmor - (resistance * resistReduction)
    return maxOf(1.0, afterResist)
}

fun calculateExplosionDamage(
    baseDamage: Double,
    distance: Double,
    radius: Double,
    falloff: Double,
    armor: Int,
    blastProt: Int,
    armorRed: Double = 0.5,
    blastProtRed: Double = 0.3,
): Double {
    val distFactor = maxOf(0.0, distance - radius) * falloff
    val damageAfterDist = maxOf(0.0, baseDamage - distFactor)
    val afterArmor = damageAfterDist - (armor * armorRed)
    val afterBlastProt = afterArmor - (blastProt * blastProtRed)
    return maxOf(1.0, afterBlastProt)
}
