package phonon.xc.util.worldguard

import org.bukkit.Location
import org.bukkit.entity.Player
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.regions.RegionContainer

var worldGuardEnabled: Boolean = false
    private set

fun initWorldGuard(): Boolean {
    return try {
        WorldGuardPlatform()
        worldGuardEnabled = true
        true
    } catch (e: Exception) {
        worldGuardEnabled = false
        false
    }
}

private object WorldGuardPlatform {
    val platform = WorldGuard.getInstance().platform
}

fun canPvp(location: Location): Boolean {
    if (!worldGuardEnabled) return true
    return try {
        val container = WorldGuard.getInstance().platform.regionContainer
        val query = container.createQuery()
        val assoc = WorldGuardPlugin.inst().wrapPlayer(WorldGuard.getInstance().platform.wrapPlayer(null))
        query.testState(location.toVector(), assoc, Flags.PVP) != false
    } catch (e: Exception) {
        true
    }
}

fun canExplode(location: Location): Boolean {
    if (!worldGuardEnabled) return true
    return try {
        val container = WorldGuard.getInstance().platform.regionContainer
        val query = container.createQuery()
        query.testState(location.toVector(), null, Flags.OTHER_EXPLOSION) != false
    } catch (e: Exception) {
        true
    }
}

fun canBuild(player: Player, location: Location): Boolean {
    if (!worldGuardEnabled) return true
    return try {
        val container = WorldGuard.getInstance().platform.regionContainer
        val query = container.createQuery()
        val wrappedPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        query.testState(location.toVector(), wrappedPlayer, Flags.BUILD) != false
    } catch (e: Exception) {
        true
    }
}
