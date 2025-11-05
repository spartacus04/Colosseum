package me.spartacus04.colosseum.listeners

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.utils.version.MinecraftServerVersion

/**
 * Represents a packet listener for the plugin that can be registered and unregistered. Requires PacketEvents
 */
open class ColosseumPacketListener(private val plugin: ColosseumPlugin) : PacketListener, InterfaceColosseumListener {
    override fun register() {
        if(MinecraftServerVersion(plugin).isRevisionAnnotationCompatible(this::class.java)) {
            PacketEvents.getAPI().eventManager.registerListener(this.asAbstract(PacketListenerPriority.NORMAL))
        } else {
            plugin.colosseumLogger.info("Could not register packet listener ${this::class.java.simpleName} due to version incompatibility.")
        }
    }

    override fun unregister() {
        PacketEvents.getAPI().eventManager.unregisterListener(this.asAbstract(PacketListenerPriority.NORMAL))
    }
}