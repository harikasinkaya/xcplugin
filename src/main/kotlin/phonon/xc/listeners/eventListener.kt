package phonon.xc.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.block.Action
import org.bukkit.entity.Player
import phonon.xc.XC
import phonon.xc.gun.FireMode
import java.util.UUID

class EventListener(private val xc: XC) : Listener {

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val item = e.item ?: return
        val action = e.action

        // Check if holding a gun
        val material = item.type
        if (material != xc.config.materialGun && material != xc.config.materialADS) return

        when (action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                // Shoot or melee attack
                val gun = getGunFromItem(item) ?: return
                
                when (gun.fireMode) {
                    FireMode.SINGLE -> {
                        xc.requestsShoot.add(ShootRequest(player.uniqueId, gun.id))
                    }
                    FireMode.BURST -> {
                        xc.requestsBurstFire.add(BurstFireRequest(player.uniqueId, gun.id))
                    }
                    FireMode.AUTO -> {
                        // Auto fire handled by right-click hold
                    }
                }
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                // Start auto fire or reload
                val gun = getGunFromItem(item) ?: return
                
                if (gun.fireMode == FireMode.AUTO) {
                    xc.requestsAutoFireStart.add(AutoFireStartRequest(player.uniqueId, gun.id))
                } else {
                    // Manual reload
                    xc.requestsReload.add(ReloadRequest(player.uniqueId, gun.id))
                }
            }
            Action.PHYSICAL -> {
                // Pressure plate - landmine activation
                val block = e.clickedBlock ?: return
                val landmine = xc.landmines[block.type] ?: return
                xc.requestsLandmineActivation.add(LandmineActivationRequest(block.location, block.type))
            }
        }
    }

    @EventHandler
    fun onPlayerSneak(e: PlayerToggleSneakEvent) {
        val player = e.player
        val item = player.inventory.itemInMainHand
        
        // Toggle ADS with sneak
        if (item.type == xc.config.materialGun || item.type == xc.config.materialADS) {
            val currentState = xc.doAimDownSights[player.uniqueId] ?: false
            xc.doAimDownSights[player.uniqueId] = !currentState
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        val damager = e.damager
        
        if (damager is Player) {
            val item = damager.inventory.itemInMainHand
            
            // Check for melee weapon
            if (item.type == xc.config.materialMelee) {
                val melee = getMeleeFromItem(item) ?: return
                xc.requestsMeleeAttack.add(MeleeAttackRequest(damager.uniqueId, melee.id, e.entity.uniqueId))
                e.isCancelled = true // Cancel vanilla damage
            }
        }
    }

    @EventHandler
    fun onPlayerItemHeld(e: PlayerItemHeldEvent) {
        val player = e.player
        val newItem = player.inventory.getItem(e.newSlot) ?: return
        
        // Gun select event
        if (newItem.type == xc.config.materialGun) {
            val gun = getGunFromItem(newItem) ?: return
            xc.requestsGunSelect.add(GunSelectRequest(player.uniqueId, gun.id))
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        val uuid = player.uniqueId
        
        // Stop crawling
        xc.playerCrawling.remove(uuid)?.let { boxEntity ->
            // Send destroy packet
        }
        
        // Clear combat log entry
        // Clean up any active states
        xc.playerAutoFiring.remove(uuid)
        xc.playerBurstFiring.remove(uuid)
        xc.playerReloading.remove(uuid)
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player = e.entity
        val uuid = player.uniqueId
        
        // Stop crawling on death
        xc.playerCrawling.remove(uuid)?.let { boxEntity ->
            // Send destroy packet
            player.isSwimming = false
        }
        
        // Record death for combat log
        // Check killer for death message
    }

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        val player = e.player
        val item = e.itemDrop.itemStack
        
        // Track dropped throwables
        if (item.type == xc.config.materialThrowable) {
            // Add to tracking system
        }
    }

    private fun getGunFromItem(item: org.bukkit.inventory.ItemStack): phonon.xc.gun.Gun? {
        // NMS ile NBT oku ve gun ID al
        // Şimdilik null döndür
        return null
    }

    private fun getMeleeFromItem(item: org.bukkit.inventory.ItemStack): phonon.xc.melee.MeleeWeapon? {
        // NMS ile NBT oku ve melee ID al
        return null
    }
}

// Request data classes
data class ShootRequest(val playerUUID: UUID, val gunId: Int)
data class ReloadRequest(val playerUUID: UUID, val gunId: Int)
data class AutoFireStartRequest(val playerUUID: UUID, val gunId: Int)
data class AutoFireStopRequest(val playerUUID: UUID)
data class BurstFireRequest(val playerUUID: UUID, val gunId: Int)
data class MeleeAttackRequest(val attackerUUID: UUID, val meleeId: Int, val targetUUID: UUID)
data class GunSelectRequest(val playerUUID: UUID, val gunId: Int)
data class LandmineActivationRequest(val location: org.bukkit.Location, val material: org.bukkit.Material)
