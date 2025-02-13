package me.spartacus04.colosseum.logging

import org.bukkit.Bukkit

/**
 * A logger for the plugin.
 *
 * @property isDebug Whether debug messages should be shown.
 * @property prefix The prefix for the messages.
 */
open class PluginLogger(private val isDebug: Boolean, prefix: String) {
    val messageFormatter = MessageFormatter(prefix)

    /**
     * Sends a confirmation message to the console.
     *
     * @param message The message to send.
     */
    fun confirm(message: String) = Bukkit.getConsoleSender().sendMessage(messageFormatter.confirm(message))

    /**
     * Sends an information message to the console.
     *
     * @param message The message to send.
     */
    fun info(message: String) = Bukkit.getConsoleSender().sendMessage(messageFormatter.info(message))

    /**
     * Sends a warning message to the console.
     *
     * @param message The message to send.
     */
    fun warn(message: String) = Bukkit.getConsoleSender().sendMessage(messageFormatter.warn(message))

    /**
     * Sends an error message to the console.
     *
     * @param message The message to send.
     */
    fun error(message: String) = Bukkit.getConsoleSender().sendMessage(messageFormatter.error(message))

    /**
     * Sends an url to the console.
     *
     * @param url The url to send.
     */
    fun url(url: String) = Bukkit.getConsoleSender().sendMessage(messageFormatter.url(url))

    /**
     * Sends a debug message to the console if debug mode is enabled.
     *
     * @param message The message to send.
     */
    fun debug(message: String) {
        if(isDebug) {
            Bukkit.getConsoleSender().sendMessage(messageFormatter.debug(message))
        }
    }
}