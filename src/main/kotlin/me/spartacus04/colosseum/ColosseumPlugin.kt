package me.spartacus04.colosseum

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import me.spartacus04.colosseum.commandHandling.ColosseumCommandRegistrant
import me.spartacus04.colosseum.i18n.ColosseumI18nManager
import me.spartacus04.colosseum.i18n.ColosseumI18nManagerBuilder
import me.spartacus04.colosseum.logging.MessageFormatter
import me.spartacus04.colosseum.logging.PluginLogger
import me.spartacus04.colosseum.scheduler.ColosseumScheduler
import me.spartacus04.colosseum.utils.version.MinecraftServerVersion
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.util.logging.Logger

abstract class ColosseumPlugin() : JavaPlugin() {
    private val prefix = this.description.prefix ?: this.name
    private val isDebug = this.description.version.contains("dev")

    /**
     * The scheduler for the plugin. Used to run tasks asynchronously or synchronously.
     */
    val scheduler = ColosseumScheduler.getScheduler(this)

    /**
     * The logger for the plugin. Used to log messages to the console.
     */
    val colosseumLogger = PluginLogger(isDebug, prefix)

    /**
     * The message formatter for the plugin. Used to format messages with the plugin prefix.
     */
    val messageFormatter = MessageFormatter(prefix)

    /**
     * The Minecraft server version the plugin is running on.
     */
    val serverVersion = MinecraftServerVersion(this)

    /**
     * The Minecraft server revision the plugin is running on.
     */
    val serverRevision = serverVersion.revision

    /**
     * The command registrant for the plugin. Used to register commands.
     */
    val commandRegistrant = ColosseumCommandRegistrant(this)

    var i18nManager: ColosseumI18nManager? = null
        private set

    /**
     * The logger for the plugin.
     */
    @Deprecated("Use `colosseumLogger` instead", ReplaceWith("colosseumLogger"))
    override fun getLogger(): Logger = super.logger

    /**
     * Checks for updates for the plugin on GitHub.
     *
     * @param repo The GitHub repository in the format "owner/repo".
     * @param consumer A function that consumes the latest version string.
     */
    fun checkForUpdates(repo: String, consumer: (String) -> Unit) {
        this.scheduler.runTaskAsynchronously {
            try {
                val reader = BufferedReader(
                    InputStreamReader(
                        URI("https://api.github.com/repos/$repo/releases/latest").toURL().openStream()
                    )
                )
                val text = reader.use {
                    it.readText()
                }

                val version = Regex("\"tag_name\": ?\"([^\"]+)\"").find(text)?.groupValues?.get(1)!!
                this.colosseumLogger.debug("Latest version: $version")
                consumer(version)
            } catch (exception: IOException) {
                this.colosseumLogger.warn("Unable to check for updates: " + exception.message)
            }
        }
    }

    /**
     * Builds the i18n manager for the plugin. If the i18n manager is already built, it returns the existing instance.
     * This should be called during plugin initialization.
     *
     * @param builderAction The action to configure the i18n manager builder.
     * @return The i18n manager.
     */
    fun buildI18nManager(builderAction: ColosseumI18nManagerBuilder.() -> Unit) = if(i18nManager != null) {
        i18nManager
    } else {
        i18nManager = ColosseumI18nManagerBuilder(this).apply(builderAction).build()
        i18nManager
    }

    /**
     * Registers commands for the plugin. This should be called during plugin initialization.
     *
     * @param commandsAction The action to configure the command registrant.
     */
    fun registerCommands(commandsAction: ColosseumCommandRegistrant.() -> Unit) {
        commandsAction(commandRegistrant)
    }

    /**
     * The singleton GSON instance for the plugin.
     */
    val gson: Gson
        get() = GSON

    companion object {
        /**
         * Singleton GSON instance for the plugin.
         */
        val GSON: Gson = GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create()
    }
}