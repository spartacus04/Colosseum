package me.spartacus04.colosseum.listeners

import me.spartacus04.colosseum.ColosseumPlugin
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

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