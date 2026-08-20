package phonon.xc.armor

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.ItemStack
import org.tomlj.TomlTable
import phonon.xc.IntoItemStack

data class Hat(
    val id: Int = 0,
    val itemName: String = "Hat",
    val itemLore: List<String> = emptyList(),
    val modelId: Int = 0,
    val armorValue: Int = 1,
    val enchants: Map<String, Int> = emptyMap()
) : IntoItemStack {
    override fun toItemStack(material: Material): ItemStack {
        val item = ItemStack(material, 1)
        val meta = item.itemMeta!!
        meta.setDisplayName(itemName)
        meta.lore = itemLore
        meta.setCustomModelData(modelId)
        
        // Armor attribute modifier ekle
        if (armorValue > 0) {
            val modifier = org.bukkit.attribute.AttributeModifier(
                java.util.UUID.randomUUID(),
                "xc_armor",
                armorValue.toDouble(),
                org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER
            )
            meta.addAttributeModifier(Attribute.ARMOR, modifier)
        }
        
        // Enchantları ekle
        for ((enchantName, level) in enchants) {
            try {
                val enchant = org.bukkit.enchantments.Enchantment.getByKey(
                    org.bukkit.NamespacedKey.minecraft(enchantName.lowercase())
                )
                if (enchant != null) {
                    meta.addEnchant(enchant, level, true)
                }
            } catch (e: Exception) {
                // Invalid enchant name
            }
        }
        
        item.itemMeta = meta
        return item
    }

    companion object {
        fun fromToml(toml: TomlTable, id: Int): Hat {
            val item = toml.getTable("item")
            val hatTable = toml.getTable("hat")
            val enchantsTable = hatTable?.getTable("enchants")
            
            val enchants = mutableMapOf<String, Int>()
            if (enchantsTable != null) {
                for (key in enchantsTable.keySet()) {
                    enchants[key] = enchantsTable.getInteger(key) ?: 1
                }
            }

            return Hat(
                id = id,
                itemName = item?.getString("name") ?: "Hat",
                itemLore = item?.getList("lore")?.filterIsInstance<String>() ?: emptyList(),
                modelId = hatTable?.getInteger("model") ?: 0,
                armorValue = hatTable?.getInteger("armor") ?: 1,
                enchants = enchants
            )
        }
    }
}
