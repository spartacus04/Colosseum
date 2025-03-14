package me.spartacus04.colosseum.i18n

import me.spartacus04.colosseum.logging.PluginLogger
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * A class that manages a plugin's i18n. This handles static and dynamic languages.
 * Static languages are languages that never change and are usually loaded from the plugin jar.
 * Dynamic languages are languages that can change at runtime and are usually loaded from a database or file.
 * The plugin will cache dynamic languages to prevent unnecessary loading, and will reload them when invalidated.
 * When a message is requested, the plugin will first check the cached dynamic languages, then the dynamic languages, and finally the static languages.
 * If the get method is called with a CommandSender, the plugin will use the forced language if it is set, otherwise it will use the player's locale.
 * If the player's locale is not found, the default language will be used. If the command sender is not a player, the default language will be used.
 *
 * @property staticLanguages The static languages.
 * @property dynamicLanguages The dynamic languages.
 * @property defaultLanguage The default language.
 * @property forcedLanguage The language to force for players.
 * @property debugMode Enables debug mode.
 * @property prefix The prefix of each message.
 */
class ColosseumI18nManager internal constructor(
    private val staticLanguages: Map<String, Map<String, String>>,
    private val dynamicLanguages: Map<String, () -> Map<String, String>>,
    private val defaultLanguage: String,
    private var forcedLanguage: String?,
    debugMode: Boolean,
    prefix: String
) {
    private val cachedDynamicLanguages = mutableMapOf<String, Map<String, String>>()
    private val pluginLogger = PluginLogger(debugMode, prefix)

    /**
     * Sets the forced language for the plugin.
     * @param lang The language to force.
     */
    fun setForcedLanguage(lang: String?) {
        forcedLanguage = lang
    }

    /**
     * Invalidates the cached dynamic languages.
     * This should be called when a dynamic language is updated.
     * This will force the plugin to reload the dynamic language.
     */
    fun invalidateDynamicLanguages() = cachedDynamicLanguages.clear()

    /**
     * Checks if the specified language is available.
     */
    fun hasLanguage(lang: String): Boolean {
        return staticLanguages.containsKey(lang) || dynamicLanguages.containsKey(lang)
    }

    /**
     * Gets the language map for the specified language.
     *
     * @param lang The language to get the map for.
     * @return The language map or null if the language is not found.
     */
    operator fun get(lang: String): Map<String, String>? {
        if(cachedDynamicLanguages.contains(lang)) {
            pluginLogger.debug("Returning cached language: $lang")
            return cachedDynamicLanguages[lang]!!
        }

        if(dynamicLanguages.containsKey(lang)) {
            pluginLogger.debug("Loading and caching dynamic language: $lang")
            val language = dynamicLanguages[lang]!!()
            cachedDynamicLanguages[lang] = language
            pluginLogger.debug("Loaded and cached dynamic language: $lang")
            return language
        }

        return staticLanguages[lang]
    }

    /**
     * Gets a message from the language file, for a specific language.
     *
     * @param lang The language to get the message from.
     * @param key The key of the message.
     * @return The message or null if the language or key is not found.
     */
    operator fun get(lang: String, key: String): String? {
        return get(lang)?.get(key)
    }

    /**
     * Gets a message from the language file, for a specific language.
     * Placeholders in the message will be replaced with the specified values.
     *
     * @param lang The language to get the message from.
     * @param key The key of the message.
     * @param placeholders The placeholders to replace in the message.
     */
    operator fun get(lang: String, key: String, vararg placeholders: Pair<String, String>): String? {
        return get(lang, key)?.let { message ->
            replacePlaceholders(message, placeholders.toMap())
        }
    }

    /**
     * Gets a message from the language file, for a specific language.
     * Placeholders in the message will be replaced with the specified values.
     * If the command sender is a player, the message will be formatted based on the player's locale, else the default language will be used.
     *
     * @param commandSender The entity that executed the command.
     * @param key The key of the message.
     * @param placeholders The placeholders to replace in the message.
     */
    operator fun get(commandSender: CommandSender, key: String, vararg placeholders: Pair<String, String>): String? {
        val lang = if(commandSender is Player) {
            if(forcedLanguage?.let { hasLanguage(it) } == true) {
                forcedLanguage!!
            } else if(hasLanguage(commandSender.locale)) {
                commandSender.locale
            } else {
                defaultLanguage
            }
        } else {
            defaultLanguage
        }

        return get(lang, key, *placeholders)
    }

    companion object {
        /**
         * Replaces placeholders in a message with the specified values.
         * EG: replacePlaceholders("Hello, %name%!", mapOf("name" to "Spartacus04")) -> "Hello, Spartacus04!"
         *
         * @param message The message to replace the placeholders in.
         * @param placeholders The placeholders to replace.
         * @return The message with the placeholders replaced.
         */
        fun replacePlaceholders(message: String, placeholders: Map<String, String>): String {
            var newMessage = message

            placeholders.forEach { (key, value) ->
                newMessage = newMessage.replace("%$key%", value)
            }

            return newMessage
        }
    }
}