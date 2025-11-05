package me.spartacus04.colosseum.i18n

import me.spartacus04.colosseum.ColosseumPlugin
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * Sends an internationalized info message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param key The key for the message in the i18n files.
 * @param placeholders The placeholders to replace in the message.
 */
fun CommandSender.sendI18nInfo(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) {
    val message = plugin.i18nManager?.get(this, key, *placeholders)
        ?: throw IllegalArgumentException("Missing translation for key '$key' in locale '${plugin.i18nManager?.getLocale(this)}'")

    this.sendMessage(plugin.messageFormatter.info(
        message
    ))
}

/**
 * Sends an internationalized confirmation message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param key The key for the message in the i18n files.
 * @param placeholders The placeholders to replace in the message.
 */
fun CommandSender.sendI18nConfirm(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) {
    val message = plugin.i18nManager?.get(this, key, *placeholders)
        ?: throw IllegalArgumentException("Missing translation for key '$key' in locale '${plugin.i18nManager?.getLocale(this)}'")

    this.sendMessage(plugin.messageFormatter.confirm(
        message
    ))
}

/**
 * Sends an internationalized warning message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param key The key for the message in the i18n files.
 * @param placeholders The placeholders to replace in the message.
 */
fun CommandSender.sendI18nWarn(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) {
    val message = plugin.i18nManager?.get(this, key, *placeholders)
        ?: throw IllegalArgumentException("Missing translation for key '$key' in locale '${plugin.i18nManager?.getLocale(this)}'")

    this.sendMessage(plugin.messageFormatter.warn(
        message
    ))
}

/**
 * Sends an internationalized error message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param key The key for the message in the i18n files.
 * @param placeholders The placeholders to replace in the message.
 */
fun CommandSender.sendI18nError(plugin: ColosseumPlugin, key: String, vararg placeholders: Pair<String, String>) {
    val message = plugin.i18nManager?.get(this, key, *placeholders)
        ?: throw IllegalArgumentException("Missing translation for key '$key' in locale '${plugin.i18nManager?.getLocale(this)}'")

    this.sendMessage(plugin.messageFormatter.error(
        message
    ))
}

/**
 * Sends an internationalized custom colored message to the command sender.
 *
 * @param plugin The ColosseumPlugin instance.
 * @param prefixColor The color of the message prefix.
 * @param messageColor The color of the message body.
 * @param key The key for the message in the i18n files.
 * @param placeholders The placeholders to replace in the message.
 */
fun CommandSender.sendI18nCustom(plugin: ColosseumPlugin, prefixColor: ChatColor, messageColor: ChatColor, key: String, vararg placeholders: Pair<String, String>) {
    val message = plugin.i18nManager?.get(this, key, *placeholders)
        ?: throw IllegalArgumentException("Missing translation for key '$key' in locale '${plugin.i18nManager?.getLocale(this)}'")

    this.sendMessage(plugin.messageFormatter.custom(
        prefixColor,
        messageColor,
        message
    ))
}