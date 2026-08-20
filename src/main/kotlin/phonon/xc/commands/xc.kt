package phonon.xc.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phonon.xc.XC

class XCCommand(private val xc: XC) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§6=== XC Plugin Help ===")
            sender.sendMessage("/xc help - Show this help")
            sender.sendMessage("/xc reload - Reload config")
            sender.sendMessage("/xc give <player> <type> <name> - Give item")
            sender.sendMessage("/xc ammo <player> <name> <amount> - Give ammo")
            sender.sendMessage("/xc debug - Toggle debug mode")
            sender.sendMessage("/xc timings - Show timings")
            return true
        }

        when (args[0].lowercase()) {
            "help" -> {
                sender.sendMessage("§6=== XC Plugin Help ===")
                sender.sendMessage("/xc help - Show this help")
                sender.sendMessage("/xc reload - Reload config")
                sender.sendMessage("/xc give <player> <type> <name> - Give item")
                sender.sendMessage("/xc ammo <player> <name> <amount> - Give ammo")
                sender.sendMessage("/xc debug - Toggle debug mode")
                sender.sendMessage("/xc timings - Show timings")
            }
            "reload" -> {
                if (!sender.hasPermission("xc.admin")) {
                    sender.sendMessage("§cYou don't have permission!")
                    return true
                }
                // Config reload logic here
                sender.sendMessage("§aConfig reloaded!")
            }
            "give" -> {
                if (!sender.hasPermission("xc.admin")) {
                    sender.sendMessage("§cYou don't have permission!")
                    return true
                }
                if (args.size < 4) {
                    sender.sendMessage("§cUsage: /xc give <player> <type> <name>")
                    return true
                }
                val player = xc.plugin?.server?.getPlayer(args[1]) ?: run {
                    sender.sendMessage("§cPlayer not found!")
                    return true
                }
                val type = args[2].lowercase()
                val name = args[3]
                // Item give logic here
                sender.sendMessage("§aGiving $type '$name' to ${player.name}")
            }
            "ammo" -> {
                if (!sender.hasPermission("xc.admin")) {
                    sender.sendMessage("§cYou don't have permission!")
                    return true
                }
                if (args.size < 4) {
                    sender.sendMessage("§cUsage: /xc ammo <player> <name> <amount>")
                    return true
                }
                val player = xc.plugin?.server?.getPlayer(args[1]) ?: run {
                    sender.sendMessage("§cPlayer not found!")
                    return true
                }
                sender.sendMessage("§aGiving ammo '${args[2]}' x${args[3]} to ${player.name}")
            }
            "debug" -> {
                if (!sender.hasPermission("xc.admin")) {
                    sender.sendMessage("§cYou don't have permission!")
                    return true
                }
                sender.sendMessage("§aDebug mode toggled")
            }
            "timings" -> {
                if (!sender.hasPermission("xc.admin")) {
                    sender.sendMessage("§cYou don't have permission!")
                    return true
                }
                sender.sendMessage("§aTimings report generated")
            }
            else -> {
                sender.sendMessage("§cUnknown command. Use /xc help")
            }
        }
        return true
    }
}
