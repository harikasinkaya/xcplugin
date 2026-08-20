package phonon.xc.util.recoil

import org.bukkit.entity.Player
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.atan2
import kotlin.math.asin

data class RecoilConfig(
    val baseRecoil: Double = 0.5,
    val horizontalRecoil: Double = 0.3,
    val recoveryRate: Double = 0.2,
    val fireRampMultiplier: Double = 1.1
)

fun calculateRecoil(config: RecoilConfig, currentMultiplier: Double = 1.0): Pair<Double, Double> {
    val vertical = config.baseRecoil * currentMultiplier
    val horizontal = (config.horizontalRecoil * currentMultiplier) * (Math.random() * 2 - 1)
    return vertical to horizontal
}

fun applyRecoil(player: Player, vertical: Double, horizontal: Double) {
    // Bukkit API ile sınırlı - NMS paketleri daha iyi sonuç verir
    val location = player.location
    val newYaw = location.yaw + horizontal.toFloat()
    val newPitch = (location.pitch + vertical.toFloat()).coerceIn(-90f, 90f)
    player.teleport(location.apply { setYaw(newYaw); pitch = newPitch })
}

fun recoverRecoil(currentRecoil: Double, recoveryRate: Double): Double {
    return maxOf(1.0, currentRecoil - recoveryRate)
}

// NMS tabanlı recoil için placeholder
// Gerçek implementasyon src/nms/v1_21/recoil.kt içinde olacak
fun sendRecoilPacketNMS(player: Any, horizontal: Double, vertical: Double) {
    // NMS packet gönderimi burada yapılacak
    // Bu bir stub - gerçek kod NMS katmanında
}
