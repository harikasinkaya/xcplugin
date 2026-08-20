package phonon.xc.util.death

import org.bukkit.entity.Player
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

data class PlayerDeathRecord(
    val victimUUID: UUID,
    val victimName: String,
    val killerUUID: UUID?,
    val killerName: String?,
    val deathMessage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isHeadshot: Boolean = false,
    val weaponId: Int = -1
)

fun formatDeathMessage(template: String, victim: String, killer: String?): String {
    return template.replace("{0}", victim).replace("{1}", killer ?: "unknown")
}

fun createPlayerHead(player: Player): org.bukkit.inventory.ItemStack? {
    return try {
        val head = org.bukkit.Material.PLAYER_HEAD.createItemStack(1)
        val meta = head.itemMeta!!
        if (meta is org.bukkit.inventory.meta.SkullMeta) {
            meta.owningPlayer = player
        }
        head.itemMeta = meta
        head
    } catch (e: Exception) {
        null
    }
}

fun saveDeathRecords(records: List<PlayerDeathRecord>, directory: String) {
    val dir = File(directory)
    if (!dir.exists()) dir.mkdirs()
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val fileName = "${dateFormat.format(Date())}_deaths.csv"
    val file = File(dir, fileName)
    
    val append = file.exists()
    FileWriter(file, append).use { writer ->
        if (!append) {
            writer.appendLine("timestamp,victim_uuid,victim_name,killer_uuid,killer_name,death_message,is_headshot,weapon_id")
        }
        for (record in records) {
            writer.appendLine(
                "${record.timestamp},${record.victimUUID},${record.victimName}," +
                "${record.killerUUID ?: ""},${record.killerName ?: ""}," +
                "\"${record.deathMessage}\",${record.isHeadshot},${record.weaponId}"
            )
        }
    }
}
