package me.spartacus04.colosseum.i18n

import com.google.gson.reflect.TypeToken
import me.spartacus04.colosseum.logging.PluginLogger
import me.spartacus04.colosseum.utils.Gson.GSON
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.jar.JarFile

open class ColosseumI18nManager(val plugin: Plugin, prefix: String, private val languagesPath: String = "langs/", debugMode: Boolean = false) : PluginLogger(debugMode, prefix) {
    private val i18nMap = HashMap<String, Map<String, String>>()
    private val defaultLanguage = "en_US"

    /**
     * If set to a language code, the plugin will use this language instead of detecting it automatically.
     */
    var forceLanguage: String? = null

    /**
     * If set to true, the plugin will use the custom language file from the plugin data folder.
     */
    var customMode: Boolean = false
        set(value) {
            if(value) {
                loadCustomLanguageFile()
            }
            field = value
        }

    /**
     * Loads the custom language file from the plugin data folder.
     *
     * If the file does not exist, it will be created and filled with the default language file.
     * If the file is outdated, it will be updated with the new keys from the default language file.
     */
    fun loadCustomLanguageFile() {
        val customFile = plugin.dataFolder.resolve("lang.json")
        val mapType = object : TypeToken<Map<String, String>>() {}.type

        if(!customFile.exists()) {
            plugin.getResource("$languagesPath$defaultLanguage.json")!!.bufferedReader().use {
                customFile.createNewFile()
                customFile.writeText(it.readText())
            }
        }

        customFile.bufferedReader().use {
            val languageMap : HashMap<String, String> = GSON.fromJson(it.readText(), mapType)

            i18nMap[defaultLanguage]!!.keys.forEach {
                if(!languageMap.contains(it)) {
                    languageMap[it] = i18nMap[defaultLanguage]!![it]!!
                }
            }

            customFile.writeText(GSON.toJson(languageMap))
            i18nMap["custom"] = languageMap
            debug("Loaded custom language file")
        }
    }

    /**
     * Loads all languages found in the plugin resources at the specified path.
     */
    fun loadLanguagesFromResources() {
        val jarFile = JarFile(File(javaClass.protectionDomain.codeSource.location.path).absolutePath.replace("%20", " "))

        jarFile.entries().asSequence().filter { it.name.startsWith(languagesPath) && it.name.endsWith(".json") }.forEach {
            val langName = it.name.replaceFirst(languagesPath, "")

            plugin.getResource("$languagesPath$langName")!!.bufferedReader().use {file ->
                val mapType = object : TypeToken<Map<String, String>>() {}.type
                val languageMap : Map<String, String> = GSON.fromJson(file.readText(), mapType)

                i18nMap[langName.replace(".json", "").lowercase()] = languageMap
                debug("Loaded language file: $langName")
            }
        }

        jarFile.close()
    }

    /**
     * Checks if the plugin has a specified language loaded.
     *
     * @param language The language to check for.
     */
    fun hasLanguage(language: String) = i18nMap.containsKey(language)

    /**
     * Gets a message from the language file. If the language is not found, the default language will be used.
     * If the key is not found in the default language, an error message will be returned.
     *
     * @param language The language to get the message from.
     * @param key The key of the message.
     * @return The message.
     */
    operator fun get(language: String, key: String): String {
        if(!hasLanguage(language)) {
            debug("Language $language not found, using default language")

            val lang = i18nMap[defaultLanguage]!!

            if(!lang.containsKey(key)) {
                error("Key $key not found in default language file")
                return messageFormatter.error("Key $key not found in language file")
            }
        }

        if(!i18nMap[language]!!.containsKey(key)) {
            error("Key $key not found in language file")
            return messageFormatter.error("Key $key not found in language file")
        }

        return i18nMap[language]!![key]!!
    }

    /**
     * Gets a formatted message from the language file. If the language is not found, the default language will be used.
     * If the key is not found in the default language, an error message will be returned.
     * Placeholders in the message will be replaced with the specified values.
     * If the command sender is a player, the message will be formatted based on the player's locale, else the default language will be used.
     * If the custom mode is enabled, the custom language file will be used.
     * If the force language is set, that language will be used.
     *
     * @param commandSender The entity that executed the command.
     * @param key The key of the message.
     * @param params The placeholders to replace in the message.
     * @return The formatted message.
     */
    fun getFormatted(commandSender: CommandSender, key: String, params: Map<String, String> = emptyMap()): String {
        if(commandSender !is Player) {
            return replacePlaceholders(get(defaultLanguage, key), params)
        }

        return if(customMode) {
            replacePlaceholders(get("custom", key), params)
        } else if(forceLanguage != null) {
            replacePlaceholders(get(forceLanguage!!, key), params)
        } else {
            @Suppress("DEPRECATION")
            replacePlaceholders(get(commandSender.locale, key), params)
        }
    }

    companion object {
        /**
         * Replaces placeholders in a message with the specified values.
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