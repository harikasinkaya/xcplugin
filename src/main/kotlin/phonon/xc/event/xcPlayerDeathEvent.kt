package phonon.xc.event

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import java.util.UUID

class xcPlayerDeathEvent(
    val victim: Player,
    val killerUUID: UUID?,
    val deathMessage: String,
    val isHeadshot: Boolean = false,
    val weaponId: Int = -1,
) : PlayerEvent(victim) {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        private val handlerList = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}

data class PlayerDeathRecord(
    val victimUUID: UUID,
    val victimName: String,
    val killerUUID: UUID?,
    val killerName: String?,
    val deathMessage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isHeadshot: Boolean = false,
    val weaponId: Int = -1,
)

data class PlayerDeathMessage(
    val message: String,
    val victim: UUID,
    val killer: UUID?
)
