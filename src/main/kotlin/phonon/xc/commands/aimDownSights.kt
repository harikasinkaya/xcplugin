package phonon.xc.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phonon.xc.XC

class AimDownSightsCommand(private val xc: XC) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be used by players!")
            return true
        }

        val player = sender
        
        if (args.isNotEmpty() && args[0].lowercase() == "off") {
            xc.doAimDownSights[player.uniqueId] = false
            player.sendMessage("§aADS disabled")
            return true
        }

        // Toggle ADS
        val currentState = xc.doAimDownSights[player.uniqueId] ?: false
        xc.doAimDownSights[player.uniqueId] = !currentState
        
        if (!currentState) {
            player.sendMessage("§aADS enabled - Right click to aim")
        } else {
            player.sendMessage("§aADS disabled")
        }
        
        return true
    }
}
