package phonon.xc

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

interface IntoItemStack {
    fun toItemStack(material: Material): ItemStack
}

// NBT anahtar sabitleri
const val ITEM_KEY_XC_FLAGS = "xc_f"
const val ITEM_KEY_XC_AMMO = "xc_a"
const val ITEM_KEY_XC_GUN_ID = "xc_id"
const val ITEM_KEY_XC_TIMESTAMP = "xc_t"
const val ITEM_KEY_XC_RELOADING = "xc_r"
const val ITEM_KEY_XC_MODEL = "xc_m"

fun createGunItem(gun: Gun, material: Material, ammo: Int = gun.ammoMax): ItemStack {
    val item = ItemStack(material, 1)
    val meta = item.itemMeta!!
    meta.setDisplayName(gun.itemName)
    meta.lore = listOf("Ammo: $ammo/${gun.ammoMax}") + gun.itemLore
    meta.setCustomModelData(gun.itemModelDefault)
    item.itemMeta = meta
    // NMS ile NBT yazılacak
    return item
}

fun setItemMetaAmmo(item: ItemStack, ammo: Int, maxAmmo: Int) {
    val meta = item.itemMeta ?: return
    meta.lore = listOf("Ammo: $ammo/$maxAmmo") + (meta.lore?.drop(1) ?: emptyList())
    item.itemMeta = meta
}

fun setItemMetaModel(item: ItemStack, modelId: Int) {
    val meta = item.itemMeta ?: return
    meta.setCustomModelData(modelId)
    item.itemMeta = meta
}
