package phonon.xc.ammo

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.tomlj.TomlTable
import phonon.xc.IntoItemStack

data class Ammo(
    val id: Int = 0,
    val itemName: String = "Ammo",
    val itemLore: List<String> = emptyList(),
    val modelId: Int = 0,
) : IntoItemStack {
    override fun toItemStack(material: Material): ItemStack {
        val item = ItemStack(material, 1)
        val meta = item.itemMeta!!
        meta.setDisplayName(itemName)
        meta.lore = itemLore
        meta.setCustomModelData(modelId)
        item.itemMeta = meta
        return item
    }

    companion object {
        fun fromToml(toml: TomlTable, id: Int): Ammo {
            val item = toml.getTable("item")
            return Ammo(
                id = id,
                itemName = item?.getString("name") ?: "Ammo",
                itemLore = item?.getList("lore")?.filterIsInstance<String>() ?: emptyList(),
                modelId = toml.getInteger("model") ?: 0,
            )
        }
    }
}
