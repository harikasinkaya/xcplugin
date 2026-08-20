package phonon.xc.melee

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.tomlj.TomlTable
import phonon.xc.IntoItemStack
import phonon.xc.util.damage.DamageType

data class MeleeWeapon(
    val id: Int = 0,
    val itemName: String = "Melee Weapon",
    val itemLore: List<String> = emptyList(),
    val itemModelDefault: Int = 0,
    val damageBase: Double = 4.0,
    val armorReduction: Double = 0.5,
    val resistReduction: Double = 0.5,
    val damageType: DamageType = DamageType.NORMAL,
    val attackSpeed: Double = 1.6,
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
        fun fromToml(toml: TomlTable, id: Int): MeleeWeapon {
            val item = toml.getTable("item")
            val model = toml.getTable("model")
            val damage = toml.getTable("damage")
            
            val damageTypeStr = damage?.getString("type") ?: "normal"
            val damageType = try {
                DamageType.valueOf(damageTypeStr.uppercase())
            } catch (e: IllegalArgumentException) {
                DamageType.NORMAL
            }

            return MeleeWeapon(
                id = id,
                itemName = item?.getString("name") ?: "Melee Weapon",
                itemLore = item?.getList("lore")?.filterIsInstance<String>() ?: emptyList(),
                itemModelDefault = model?.getInteger("default") ?: 0,
                damageBase = damage?.getDouble("base") ?: 4.0,
                armorReduction = damage?.getDouble("armor_reduction") ?: 0.5,
                resistReduction = damage?.getDouble("resist_reduction") ?: 0.5,
                damageType = damageType,
                attackSpeed = damage?.getDouble("attack_speed") ?: 1.6,
            )
        }
    }
}
