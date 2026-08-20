package phonon.xc

import org.bukkit.Material
import org.tomlj.TomlTable
import java.util.EnumMap
import java.util.logging.Logger

data class XCConfig(
    val configPathAmmo: String = "ammo",
    val configPathGun: String = "gun",
    val configPathMelee: String = "melee",
    val configPathThrowable: String = "throwable",
    val configPathLandmine: String = "landmine",
    val configPathArmor: String = "armor",
    val materialGun: Material = Material.WARPED_FUNGUS_ON_A_STICK,
    val materialADS: Material = Material.CARROT_ON_A_STICK,
    val materialMelee: Material = Material.IRON_SWORD,
    val materialThrowable: Material = Material.GOLDEN_HORSE_ARMOR,
    val materialAmmo: Material = Material.SNOWBALL,
    val materialArmor: Material = Material.LEATHER_HORSE_ARMOR,
    val maxTypesAmmo: Int = 512,
    val maxTypesGun: Int = 1024,
    val maxTypesMelee: Int = 1024,
    val maxTypesThrowable: Int = 1024,
    val maxTypesHat: Int = 1024,
    val deathMessageExplosion: String = "{0} was killed in an explosion",
    val deathMessageWither: String = "{0} suffocated in poison gas",
    val dropPlayerHeadOnDeath: Boolean = true,
    val deathLogSaveDir: String = "plugins/xc/logs",
    val deathLogSaveInterval: Int = 1200,
    val gunAutoFireMaxTicksSinceLastRequest: Int = 4,
    val recoilRecoveryRate: Double = 0.2,
    val autoReloadGuns: Boolean = true,
    val autoFireTicksBeforeReload: Int = 2,
    val landmineDisableDrop: Boolean = true,
    val blockDamageExplosion: Boolean = true,
    val soundOnHit: String = "minecraft:entity.experience_orb.pickup",
    val soundOnHitEnabled: Boolean = true,
    val soundOnHitVolume: Float = 1.0f,
    val enforceArmor: Boolean = false,
    val armorValues: EnumMap<Material, Int> = EnumMap(Material::class.java),
    val antiCombatLogEnabled: Boolean = true,
    val antiCombatLogTimeout: Double = 20.0,
    val swayMovementSpeedDecay: Double = 0.5,
    val swayMovementThreshold: Double = 3.0,
    val crawlOnlyOnCrawlWeapons: Boolean = false,
    val doTimingsDefault: Boolean = false,
    val asyncPackets: Boolean = true,
    val numProjectileThreads: Int = 4,
) {
    companion object {
        fun load(toml: TomlTable, logger: Logger): XCConfig {
            val configs = toml.getTable("configs")
            val material = toml.getTable("material")
            val maxTypes = toml.getTable("max_types")
            val deaths = toml.getTable("deaths")
            val gunCfg = toml.getTable("gun")
            val landmineCfg = toml.getTable("landmine")
            val blockDamage = toml.getTable("block_damage")
            val sound = toml.getTable("sound")
            val armor = toml.getTable("armor")
            val armorValues = toml.getTable("armor.values")
            val antiCombatLog = toml.getTable("anti_combat_log")
            val sway = toml.getTable("sway")
            val crawl = toml.getTable("crawl")
            val debug = toml.getTable("debug")
            val experimental = toml.getTable("experimental")

            val armorVals = EnumMap<Material, Int>(Material::class.java)
            if (armorValues != null) {
                for (key in armorValues.keySet()) {
                    try {
                        val mat = Material.valueOf(key.uppercase())
                        armorVals[mat] = armorValues.getInteger(key) ?: 0
                    } catch (e: IllegalArgumentException) {
                        logger.warning("Unknown material for armor value: $key")
                    }
                }
            }

            return XCConfig(
                configPathAmmo = configs?.getString("ammo") ?: "ammo",
                configPathGun = configs?.getString("gun") ?: "gun",
                configPathMelee = configs?.getString("melee") ?: "melee",
                configPathThrowable = configs?.getString("throwable") ?: "throwable",
                configPathLandmine = configs?.getString("landmine") ?: "landmine",
                configPathArmor = configs?.getString("armor") ?: "armor",
                materialGun = Material.valueOf(material?.getString("gun") ?: "WARPED_FUNGUS_ON_A_STICK"),
                materialADS = Material.valueOf(material?.getString("aim_down_sights") ?: "CARROT_ON_A_STICK"),
                materialMelee = Material.valueOf(material?.getString("melee") ?: "IRON_SWORD"),
                materialThrowable = Material.valueOf(material?.getString("throwable") ?: "GOLDEN_HORSE_ARMOR"),
                materialAmmo = Material.valueOf(material?.getString("ammo") ?: "SNOWBALL"),
                materialArmor = Material.valueOf(material?.getString("armor") ?: "LEATHER_HORSE_ARMOR"),
                maxTypesAmmo = maxTypes?.getInteger("ammo") ?: 512,
                maxTypesGun = maxTypes?.getInteger("gun") ?: 1024,
                maxTypesMelee = maxTypes?.getInteger("melee") ?: 1024,
                maxTypesThrowable = maxTypes?.getInteger("throwable") ?: 1024,
                maxTypesHat = maxTypes?.getInteger("hat") ?: 1024,
                deathMessageExplosion = deaths?.getString("message_explosion") ?: "{0} was killed in an explosion",
                deathMessageWither = deaths?.getString("message_wither") ?: "{0} suffocated in poison gas",
                dropPlayerHeadOnDeath = deaths?.getBoolean("drop_head") ?: true,
                deathLogSaveDir = deaths?.getString("log_save_dir") ?: "plugins/xc/logs",
                deathLogSaveInterval = deaths?.getInteger("save_interval") ?: 1200,
                gunAutoFireMaxTicksSinceLastRequest = gunCfg?.getInteger("auto_fire_max_ticks_since_last_request") ?: 4,
                recoilRecoveryRate = gunCfg?.getDouble("recoil_recovery_rate") ?: 0.2,
                autoReloadGuns = gunCfg?.getBoolean("auto_reload_guns") ?: true,
                autoFireTicksBeforeReload = gunCfg?.getInteger("auto_fire_ticks_before_reload") ?: 2,
                landmineDisableDrop = landmineCfg?.getBoolean("disable_drop") ?: true,
                blockDamageExplosion = blockDamage?.getBoolean("explosion") ?: true,
                soundOnHit = sound?.getString("on_hit") ?: "minecraft:entity.experience_orb.pickup",
                soundOnHitEnabled = sound?.getBoolean("on_hit_enabled") ?: true,
                soundOnHitVolume = (sound?.getDouble("on_hit_volume") ?: 1.0).toFloat(),
                enforceArmor = armor?.getBoolean("enforce") ?: false,
                armorValues = armorVals,
                antiCombatLogEnabled = antiCombatLog?.getBoolean("enabled") ?: true,
                antiCombatLogTimeout = antiCombatLog?.getDouble("timeout") ?: 20.0,
                swayMovementSpeedDecay = sway?.getDouble("movement_speed_decay") ?: 0.5,
                swayMovementThreshold = sway?.getDouble("movement_threshold") ?: 3.0,
                crawlOnlyOnCrawlWeapons = crawl?.getBoolean("only_allowed_on_crawl_weapons") ?: false,
                doTimingsDefault = debug?.getBoolean("do_timings_default") ?: false,
                asyncPackets = experimental?.getBoolean("async_packets") ?: true,
                numProjectileThreads = experimental?.getInteger("num_projectile_threads") ?: 4,
            )
        }
    }
}
