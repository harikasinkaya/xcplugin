package phonon.xc.throwable

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.tomlj.TomlTable
import phonon.xc.IntoItemStack
import phonon.xc.util.damage.DamageType

data class ThrowableItem(
    val id: Int = 0,
    val itemName: String = "Throwable",
    val itemLore: List<String> = emptyList(),
    val itemModelDefault: Int = 0,
    val fuseTimeTicks: Int = 60,
    val damageHolderOnExpired: Boolean = false,
    val damageBase: Double = 5.0,
    val damageType: DamageType = DamageType.EXPLOSION,
    val explosionRadius: Double = 4.0,
    val explosionFalloff: Double = 1.0,
    val throwVelocity: Double = 1.5,
    val handlers: List<String> = listOf("explosion"),
) : IntoItemStack {
    override fun toItemStack(material: Material): ItemStack {
        val item = ItemStack(material, 1)
        val meta = item.itemMeta!!
        meta.setDisplayName(itemName)
        meta.lore = itemLore
        meta.setCustomModelData(itemModelDefault)
        item.itemMeta = meta
        return item
    }

    companion object {
        fun fromToml(toml: TomlTable, id: Int): ThrowableItem {
            val item = toml.getTable("item")
            val model = toml.getTable("model")
            val throwTbl = toml.getTable("throw")
            val handlersTable = toml.getTable("handlers")
            val explosion = toml.getTable("explosion")

            val handlers = handlersTable?.getList("list")?.filterIsInstance<String>() ?: listOf("explosion")
            
            return ThrowableItem(
                id = id,
                itemName = item?.getString("name") ?: "Throwable",
                itemLore = item?.getList("lore")?.filterIsInstance<String>() ?: emptyList(),
                itemModelDefault = model?.getInteger("default") ?: 0,
                fuseTimeTicks = throwTbl?.getInteger("fuse_ticks") ?: 60,
                damageHolderOnExpired = throwTbl?.getBoolean("damage_holder_on_expired") ?: false,
                damageBase = explosion?.getDouble("damage") ?: 5.0,
                explosionRadius = explosion?.getDouble("radius") ?: 4.0,
                explosionFalloff = explosion?.getDouble("falloff") ?: 1.0,
                throwVelocity = throwTbl?.getDouble("velocity") ?: 1.5,
                handlers = handlers,
            )
        }
    }
}

data class ThrowableState(
    val ownerUUID: java.util.UUID,
    val throwableId: Int,
    var fuseTicksRemaining: Int,
    val location: org.bukkit.Location,
    val velocity: org.bukkit.util.Vector,
    var isThrown: Boolean = false
)
