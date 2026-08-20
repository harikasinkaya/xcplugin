package phonon.xc.landmine

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.tomlj.TomlTable
import phonon.xc.IntoItemStack
import phonon.xc.util.damage.DamageType

data class Landmine(
    val material: Material = Material.STONE_PRESSURE_PLATE,
    val itemName: String = "Landmine",
    val itemLore: List<String> = emptyList(),
    val itemModelDefault: Int = 0,
    val damageBase: Double = 8.0,
    val damageType: DamageType = DamageType.LANDMINE,
    val explosionRadius: Double = 3.0,
    val explosionFalloff: Double = 1.5,
    val armorReduction: Double = 0.5,
    val blastProtReduction: Double = 0.3,
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
        fun fromToml(toml: TomlTable): Landmine? {
            val matStr = toml.getString("material") ?: return null
            val material = try {
                Material.valueOf(matStr.uppercase())
            } catch (e: IllegalArgumentException) {
                Material.STONE_PRESSURE_PLATE
            }
            
            val item = toml.getTable("item")
            val model = toml.getTable("model")
            val explosion = toml.getTable("explosion")

            return Landmine(
                material = material,
                itemName = item?.getString("name") ?: "Landmine",
                itemLore = item?.getList("lore")?.filterIsInstance<String>() ?: emptyList(),
                itemModelDefault = model?.getInteger("default") ?: 0,
                damageBase = explosion?.getDouble("damage") ?: 8.0,
                explosionRadius = explosion?.getDouble("radius") ?: 3.0,
                explosionFalloff = explosion?.getDouble("falloff") ?: 1.5,
                armorReduction = explosion?.getDouble("armor_reduction") ?: 0.5,
                blastProtReduction = explosion?.getDouble("blast_prot_reduction") ?: 0.3,
            )
        }
    }
}

data class LandmineActivationRequest(
    val location: org.bukkit.Location,
    val landmineMaterial: Material
)
