package me.spartacus04.colosseum.logging

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.i18n.sendI18nConfirm
import me.spartacus04.colosseum.i18n.sendI18nInfo
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
     * Sends a url to the console.
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

    /**
     * Sends an internationalized confirmation message to the console.
     *
     * @param plugin The ColosseumPlugin instance.
     * @param key The i18n key.
     * @param placeholders The placeholders to replace in the message.
     */
    fun confirmI18n(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) =
        Bukkit.getConsoleSender().sendI18nConfirm(plugin, key, *placeholders)

    /**
     * Sends an internationalized information message to the console.
     *
     * @param plugin The ColosseumPlugin instance.
     * @param key The i18n key.
     * @param placeholders The placeholders to replace in the message.
     */
    fun infoI18n(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) =
        Bukkit.getConsoleSender().sendI18nInfo(plugin, key, *placeholders)

    /**
     * Sends an internationalized warning message to the console.
     *
     * @param plugin The ColosseumPlugin instance.
     * @param key The i18n key.
     * @param placeholders The placeholders to replace in the message.
     */
    fun warnI18n(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) =
        Bukkit.getConsoleSender().sendI18nInfo(plugin, key, *placeholders)

    /**
     * Sends an internationalized error message to the console.
     *
     * @param plugin The ColosseumPlugin instance.
     * @param key The i18n key.
     * @param placeholders The placeholders to replace in the message.
     */
    fun errorI18n(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) =
        Bukkit.getConsoleSender().sendI18nInfo(plugin, key, *placeholders)
}