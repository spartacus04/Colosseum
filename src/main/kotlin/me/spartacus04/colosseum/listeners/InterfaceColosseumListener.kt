package me.spartacus04.colosseum.listeners

/**
 * Represents a listener for the plugin that can be registered and unregistered.
 */
interface InterfaceColosseumListener {
    /**
     * Registers the listener.
     */
    fun register()

    /**
     * Unregisters the listener.
     */
    fun unregister()
}