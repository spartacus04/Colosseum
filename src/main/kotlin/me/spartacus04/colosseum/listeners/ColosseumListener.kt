package me.spartacus04.colosseum.listeners

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.utils.version.MinecraftServerVersion
import me.spartacus04.colosseum.utils.version.VersionCompatibilityMin
import me.spartacus04.colosseum.utils.version.VersionCompatibilityRange
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

/**
 * Represents a listener for the spigot API that can be registered and unregistered.
 */
open class ColosseumListener(private val plugin: ColosseumPlugin) : Listener, InterfaceColosseumListener {
    override fun register() {
        if(plugin.serverVersion.isVersionAnnotationCompatible(this::class.java)) {
            plugin.server.pluginManager.registerEvents(this, plugin)
        } else {
            plugin.colosseumLogger.debug("Could not register listener ${this::class.java.simpleName} due to version incompatibility.")
        }
    }

    override fun unregister() {
        HandlerList.unregisterAll(this)
    }
}