package phonon.xc.util.blockcrack

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

data class BlockCrackAnimation(
    val block: Block,
    val stage: Int = 0, // 0-9 arası çatlama seviyesi
    val location: Location = block.location
)

fun sendBlockCrackPacket(player: Player, animation: BlockCrackAnimation) {
    // NMS paketi ile gönderilecek
    // ClientboundBlockDestructionPacket
    // Bu bir stub - gerçek implementasyon NMS katmanında
}

fun broadcastBlockCrack(location: Location, stage: Int, radius: Double = 64.0) {
    val world = location.world ?: return
    val nearbyPlayers = world.getNearbyPlayers(location, radius)
    
    for (player in nearbyPlayers) {
        // NMS paketi gönder
        // sendBlockCrackPacket(player, BlockCrackAnimation(location.block, stage))
    }
}
