package me.spartacus04.colosseum.i18n

import com.google.gson.reflect.TypeToken
import me.spartacus04.colosseum.utils.Gson.GSON
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.jar.JarFile

/**
 * A builder class for the i18n manager. This class is used to build the i18n manager with the specified settings.
 * The i18n manager is used to manage the languages for the plugin.
 *
 * @property plugin The plugin instance.
 */
@Suppress("unused")
class ColosseumI18nManagerBuilder(private val plugin: JavaPlugin) {
    private var defaultLanguage: String = "en_US"
    private var staticLanguages: MutableMap<String, Map<String, String>> = mutableMapOf()
    private var dynamicLanguages: MutableMap<String, () -> Map<String, String>> = mutableMapOf()
    private var forceLanguage: String? = null
    private var debugMode: Boolean = false
    private var prefix: String = plugin.name
    private var languagesToLower: Boolean = true

    /**
     * Sets the default locale file for the plugin.
     *
     * @param defaultLocale The default language name.
     * @return The builder instance.
     */
    fun setDefaultLocale(defaultLocale: String) = apply {
        this.defaultLanguage = defaultLocale
    }

    /**
     * Loads the language files from the specified directory in the plugin jar.
     *
     * @param directory The directory to load the language files from.
     * @return The builder instance.
     */
    fun loadInternalLanguageDirectory(directory: String) = apply {
        staticLanguages.putAll(parseLanguageDirectoryFromJar(directory))
    }

    /**
     * Loads the language files from the specified paths in the plugin jar.
     *
     * @param languages The languages to load.
     * @return The builder instance.
     */
    fun loadInternalLanguages(vararg languages: String) = apply {
        languages.forEach {
            val lang = parseLanguageFromJar("$it.json", it)
            staticLanguages[lang.first] = lang.second
        }
    }

    /**
     * Adds the specified languages to the static languages.
     *
     * @param languages The languages to add.
     * @return The builder instance.
     */
    fun addStaticLanguage(vararg languages: Pair<String, Map<String, String>>) = apply {
        staticLanguages.putAll(languages)
    }

    /**
     * Loads the language files from the specified directory in the plugin data folder.
     * If the file does not exist, it will be created and filled with the base language file.
     *
     * @param langFile The file to load the language from.
     * @param name The name of the language.
     * @param baseLangFile The internal path to the base language file to use.
     * @return The builder instance.
     */
    fun loadExternalLanguageFiles(langFile: File, name: String, baseLangFile: String) = apply {
        dynamicLanguages.put(name) {
            val hashMapType = object : TypeToken<HashMap<String, String>>() {}.type

            if(!langFile.exists()) {
                plugin.getResource(baseLangFile)!!.bufferedReader().use {
                    langFile.createNewFile()
                    langFile.writeText(it.readText())
                }
            }

            langFile.bufferedReader().use { bufferedReader ->
                val languageMap : HashMap<String, String> = GSON.fromJson(bufferedReader.readText(), hashMapType)

                return@put languageMap
            }
        }
    }

    /**
     * Adds a dynamic language to the manager.
     *
     * @param lang The language name.
     * @param fetchLang The function to fetch the language.
     * @return The builder instance.
     */
    fun addDynamicLanguage(lang: String, fetchLang: () -> Map<String, String>) = apply {
        dynamicLanguages[lang] = fetchLang
    }

    /**
     * Forces the language to be used by the plugin.
     * This will override the default language.
     * If the language is not found, the default language will be used.
     *
     * @param language The language to force.
     * @return The builder instance.
     */
    fun forceLanguage(language: String) = apply {
        this.forceLanguage = language
    }

    /**
     * Sets the debug mode for the i18n manager.
     *
     * @param debugMode The debug mode.
     * @return The builder instance.
     */
    fun debugMode(debugMode: Boolean) = apply {
        this.debugMode = debugMode
    }

    /**
     * Sets the prefix for the plugin.
     * If not set, the plugin name will be used.
     *
     * @param prefix The prefix to use.
     */
    fun setPrefix(prefix: String) = apply {
        this.prefix = prefix
    }

    /**
     * Sets whether the languages should be converted to lowercase.
     * This is useful for case-insensitive language handling.
     *
     * @param languagesToLower Whether the languages should be converted to lowercase.
     * @return The builder instance.
     */
    fun setLanguagesToLower(languagesToLower: Boolean) = apply {
        this.languagesToLower = languagesToLower
    }

    private fun parseLanguageFromJar(path: String, name: String): Pair<String, Map<String, String>> {
        plugin.getResource(path)!!.bufferedReader().use {file ->
            val mapType = object : TypeToken<Map<String, String>>() {}.type
            val languageMap : Map<String, String> = GSON.fromJson(file.readText(), mapType)

            return Pair(name, languageMap)
        }
    }

    private fun parseLanguageDirectoryFromJar(path: String) : MutableMap<String, Map<String, String>> {
        val jarFile = JarFile(File(javaClass.protectionDomain.codeSource.location.path).absolutePath.replace("%20", " "))

        val languageMap = jarFile.entries().asSequence().filter { it.name.startsWith(path) && it.name.endsWith(".json") }.map {
            val langName = it.name.replaceFirst(path, "")

            parseLanguageFromJar("$path$langName", langName.replace(".json", ""))

        }

        jarFile.close()

        return languageMap.toMap().toMutableMap()
    }

    /**
     * Builds the i18n manager with the specified settings.
     * This should be called after all settings are set.
     *
     * @return The i18n manager.
     */
    fun build(): ColosseumI18nManager {
        if(languagesToLower) {
            this.defaultLanguage = this.defaultLanguage.lowercase()

            staticLanguages = staticLanguages.mapKeys { it.key.lowercase() }.toMutableMap()
            dynamicLanguages = dynamicLanguages.mapKeys { it.key.lowercase() }.toMutableMap()
        }

        return ColosseumI18nManager(
            staticLanguages,
            dynamicLanguages,
            defaultLanguage,
            forceLanguage,
            debugMode,
            prefix
        )
    }
}