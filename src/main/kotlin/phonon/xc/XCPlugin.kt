package phonon.xc

import org.bukkit.plugin.java.JavaPlugin

class XCPlugin : JavaPlugin() {
    companion object {
        internal lateinit var plugin: XCPlugin
    }

    override fun onEnable() {
        plugin = this
        XC.onEnable(this)
    }

    override fun onDisable() {
        XC.onDisable(this)
    }
}
