package me.spartacus04.colosseum.logging

import me.spartacus04.colosseum.ColosseumPlugin
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * Sends an info message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param message The message to send.
 */
fun CommandSender.sendInfo(plugin: ColosseumPlugin, message: String) {
    this.sendMessage(plugin.messageFormatter.info(message))
}

/**
 * Sends a confirmation message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param message The message to send.
 */
fun CommandSender.sendConfirm(plugin: ColosseumPlugin, message: String) {
    this.sendMessage(plugin.messageFormatter.confirm(message))
}

/**
 * Sends a warning message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param message The message to send.
 */
fun CommandSender.sendWarn(plugin: ColosseumPlugin, message: String) {
    this.sendMessage(plugin.messageFormatter.warn(message))
}

/**
 * Sends an error message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param message The message to send.
 */
fun CommandSender.sendError(plugin: ColosseumPlugin, message: String) {
    this.sendMessage(plugin.messageFormatter.error(message))
}

/**
 * Sends a custom colored message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param prefixColor The color of the prefix.
 * @param messageColor The color of the message.
 * @param message The message to send.
 */
fun CommandSender.sendCustom(plugin: ColosseumPlugin, prefixColor: ChatColor, messageColor: ChatColor, message: String) {
    this.sendMessage(plugin.messageFormatter.custom(prefixColor, messageColor, message))
}

/**
 * Sends a url message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param url The url to send.
 */
fun CommandSender.sendUrl(plugin: ColosseumPlugin, url: String) {
    this.sendMessage(plugin.messageFormatter.url(url))
}
