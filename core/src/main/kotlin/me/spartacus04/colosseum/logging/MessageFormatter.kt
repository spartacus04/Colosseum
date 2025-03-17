package me.spartacus04.colosseum.logging

import org.bukkit.ChatColor

/**
 * Formats messages with a prefix and color.
 *
 * @property prefix The prefix.
 */
class MessageFormatter(private val prefix: String) {
    /**
     * Formats a message with the prefix and the message with the color green.
     *
     * @param message The message.
     */
    fun confirm(message: String) = "[§a$prefix§r] §a$message"

    /**
     * Formats a message with the prefix being the color green and the message being the color white.
     *
     * @param message The message.
     */
    fun info(message: String) = "[§a$prefix§r] §r$message"

    /**
     * Formats a message with the prefix and the message being the color yellow.
     *
     * @param message The message.
     */
    fun warn(message: String) = "[§6$prefix§r] §6$message"

    /**
     * Formats a message with the prefix and the message being the color red.
     *
     * @param message The message.
     */
    fun error(message: String) = "[§c$prefix§r] §c$message"

    /**
     * Formats a message with the prefix and the message being the color gray.
     *
     * @param message The message.
     */
    fun debug(message: String) = "[§7$prefix§r] §7$message"

    /**
     * Formats a message with a custom prefix color, message color, and message.
     *
     * @param prefixColor The color of the prefix.
     * @param messageColor The color of the message.
     * @param message The message.
     */
    fun custom(prefixColor: ChatColor, messageColor: ChatColor, message: String) = "[$prefixColor$prefix§r] $messageColor$message"

    /**
     * Formats a url
     *
     * @param url The url
     */
    fun url(url: String) = "§6[§2$url§6]"
}