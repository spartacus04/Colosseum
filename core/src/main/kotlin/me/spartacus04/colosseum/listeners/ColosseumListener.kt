package me.spartacus04.colosseum.listeners

import me.spartacus04.colosseum.utils.version.MinecraftServerVersion
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

/**
 * Represents a listener for the spigot API that can be registered and unregistered.
 */
open class ColosseumListener(private val plugin: Plugin, private val minVersion: MinecraftServerVersion? = null) : Listener, InterfaceColosseumListener {
    override fun register() {
        if(minVersion == null || MinecraftServerVersion.current >= minVersion) {
            plugin.server.pluginManager.registerEvents(this, plugin)
        }
    }

    override fun unregister() {
        HandlerList.unregisterAll(this)
    }
}