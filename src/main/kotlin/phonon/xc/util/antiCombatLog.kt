package phonon.xc.util.anticombatlog

import org.bukkit.entity.Player
import java.util.UUID

data class CombatLoggerEntry(
    val playerUUID: UUID,
    val playerName: String,
    var lastDamageTime: Long = System.currentTimeMillis(),
    var attackerUUID: UUID? = null,
    var attackerName: String? = null
)

class CombatLogTracker(
    private val timeoutSeconds: Double = 20.0
) {
    private val entries: MutableMap<UUID, CombatLoggerEntry> = mutableMapOf()

    fun recordDamage(victim: Player, attacker: Player?) {
        val entry = entries.getOrPut(victim.uniqueId) {
            CombatLoggerEntry(victim.uniqueId, victim.name)
        }
        entry.lastDamageTime = System.currentTimeMillis()
        entry.attackerUUID = attacker?.uniqueId
        entry.attackerName = attacker?.name
    }

    fun isInCombat(player: Player): Boolean {
        val entry = entries[player.uniqueId] ?: return false
        val elapsed = (System.currentTimeMillis() - entry.lastDamageTime) / 1000.0
        return elapsed < timeoutSeconds
    }

    fun getEntry(player: Player): CombatLoggerEntry? = entries[player.uniqueId]

    fun removePlayer(player: Player): CombatLoggerEntry? = entries.remove(player.uniqueId)

    fun getExpiredEntries(): List<CombatLoggerEntry> {
        val now = System.currentTimeMillis()
        val timeoutMs = (timeoutSeconds * 1000).toLong()
        return entries.values.filter { now - it.lastDamageTime > timeoutMs }
    }

    fun cleanupExpired() {
        getExpiredEntries().forEach { entries.remove(it.playerUUID) }
    }
}
