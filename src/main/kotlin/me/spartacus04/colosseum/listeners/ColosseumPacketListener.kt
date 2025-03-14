package me.spartacus04.colosseum.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import me.spartacus04.colosseum.utils.version.MinecraftServerVersion
import org.bukkit.plugin.Plugin

/**
 * Represents a packet listener for the plugin that can be registered and unregistered. Requires ProtocolLib.
 */
open class ColosseumPacketListener(private val plugin: Plugin, private val minVersion: MinecraftServerVersion? = null, packetType: PacketType, listenerPriority: ListenerPriority = ListenerPriority.NORMAL) : PacketAdapter(plugin, listenerPriority, packetType), InterfaceColosseumListener {
    override fun register() {
        if(minVersion == null || MinecraftServerVersion.current >= minVersion) {
            ProtocolLibrary.getProtocolManager().addPacketListener(this)
        }
    }

    override fun unregister() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this)
    }
}