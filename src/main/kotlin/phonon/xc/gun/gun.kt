package phonon.xc.gun

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.tomlj.TomlTable
import phonon.xc.IntoItemStack
import phonon.xc.ammo.Ammo

data class Gun(
    val id: Int = 0,
    val itemName: String = "Gun",
    val itemLore: List<String> = emptyList(),
    val itemModelDefault: Int = 0,
    val itemModelAds: Int = 0,
    val itemModelEmpty: Int = 0,
    val itemModelReload: Int = 0,
    val deathMessage: String = "{0} was shot by {1}",
    val equipDelayTicks: Int = 20,
    val requiresCrawl: Boolean = false,
    val ammoType: Ammo? = null,
    val ammoMax: Int = 30,
    val reloadTimeTicks: Int = 60,
    val fireMode: FireMode = FireMode.SINGLE,
    val burstCount: Int = 3,
    val burstDelays: List<Int> = listOf(5, 10),
    val autoFireDelay: Int = 5,
    val swayBase: Double = 1.0,
    val swayMovementMultiplier: Double = 0.5,
    val swayAdsMultiplier: Double = 0.3,
    val recoilBase: Double = 0.5,
    val recoilHorizontal: Double = 0.3,
    val recoilRecoveryRate: Double = 0.2,
    val projectileSpeed: Double = 50.0,
    val projectileGravity: Double = 0.0,
    val projectileLifetime: Int = 100,
    val projectileRange: Double = 100.0,
    val damageBase: Double = 6.0,
    val headshotMultiplier: Double = 2.0,
    val armorReduction: Double = 0.5,
    val resistReduction: Double = 0.5,
) : IntoItemStack {
    override fun toItemStack(material: Material): ItemStack {
        return phonon.xc.createGunItem(this, material, ammoMax)
    }

    companion object {
        fun fromToml(toml: TomlTable, id: Int): Gun {
            val item = toml.getTable("item")
            val model = toml.getTable("model")
            val death = toml.getTable("death")
            val equip = toml.getTable("equip")
            val crawl = toml.getTable("crawl")
            val ammo = toml.getTable("ammo")
            val reload = toml.getTable("reload")
            val shoot = toml.getTable("shoot")
            val automatic = toml.getTable("automatic")
            val sway = toml.getTable("sway")
            val recoil = toml.getTable("recoil")
            val projectile = toml.getTable("projectile")

            val fireModeStr = shoot?.getString("mode") ?: "single"
            val fireMode = when (fireModeStr.lowercase()) {
                "auto" -> FireMode.AUTO
                "burst" -> FireMode.BURST
                else -> FireMode.SINGLE
            }

            return Gun(
                id = id,
                itemName = item?.getString("name") ?: "Gun",
                itemLore = item?.getList("lore")?.filterIsInstance<String>() ?: emptyList(),
                itemModelDefault = model?.getInteger("default") ?: 0,
                itemModelAds = model?.getInteger("ads") ?: 0,
                itemModelEmpty = model?.getInteger("empty") ?: 0,
                itemModelReload = model?.getInteger("reload") ?: 0,
                deathMessage = death?.getString("message") ?: "{0} was shot by {1}",
                equipDelayTicks = equip?.getInteger("delay_ticks") ?: 20,
                requiresCrawl = crawl?.getBoolean("required") ?: false,
                ammoMax = ammo?.getInteger("max") ?: 30,
                reloadTimeTicks = reload?.getInteger("time_ticks") ?: 60,
                fireMode = fireMode,
                burstCount = automatic?.getInteger("burst_count") ?: 3,
                burstDelays = automatic?.getList("burst_delays")?.filterIsInstance<Long>()?.map { it.toInt() } ?: listOf(5, 10),
                autoFireDelay = automatic?.getInteger("delay") ?: 5,
                swayBase = sway?.getDouble("base") ?: 1.0,
                swayMovementMultiplier = sway?.getDouble("movement_multiplier") ?: 0.5,
                swayAdsMultiplier = sway?.getDouble("ads_multiplier") ?: 0.3,
                recoilBase = recoil?.getDouble("base") ?: 0.5,
                recoilHorizontal = recoil?.getDouble("horizontal") ?: 0.3,
                recoilRecoveryRate = recoil?.getDouble("recovery_rate") ?: 0.2,
                projectileSpeed = projectile?.getDouble("speed") ?: 50.0,
                projectileGravity = projectile?.getDouble("gravity") ?: 0.0,
                projectileLifetime = projectile?.getInteger("lifetime") ?: 100,
                projectileRange = projectile?.getDouble("range") ?: 100.0,
                damageBase = shoot?.getDouble("damage") ?: 6.0,
                headshotMultiplier = shoot?.getDouble("headshot_multiplier") ?: 2.0,
                armorReduction = shoot?.getDouble("armor_reduction") ?: 0.5,
                resistReduction = shoot?.getDouble("resist_reduction") ?: 0.5,
            )
        }
    }
}

enum class FireMode { SINGLE, BURST, AUTO }
