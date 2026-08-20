package phonon.xc.util.sound

import org.bukkit.Location
import org.bukkit.entity.Player
import org.tomlj.TomlTable

data class XCSound(
    val sound: String = "minecraft:entity.generic.explode",
    val volume: Float = 1.0f,
    val pitch: Float = 1.0f,
    val enabled: Boolean = true
) {
    fun play(location: Location) {
        if (!enabled) return
        location.world?.playSound(location, sound, volume, pitch)
    }

    fun play(player: Player) {
        if (!enabled) return
        player.playSound(player.location, sound, volume, pitch)
    }

    companion object {
        fun fromToml(toml: TomlTable?): XCSound {
            if (toml == null) return XCSound()
            return when {
                toml.isString("sound") -> XCSound(
                    sound = toml.getString("sound") ?: "minecraft:entity.generic.explode",
                    volume = 1.0f,
                    pitch = 1.0f
                )
                toml.isTable("") -> XCSound(
                    sound = toml.getString("sound") ?: "minecraft:entity.generic.explode",
                    volume = (toml.getDouble("volume") ?: 1.0).toFloat(),
                    pitch = (toml.getDouble("pitch") ?: 1.0).toFloat(),
                    enabled = toml.getBoolean("enabled") ?: true
                )
                else -> XCSound()
            }
        }
    }
}
