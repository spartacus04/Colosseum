package me.spartacus04.colosseum.utils

import me.spartacus04.colosseum.logging.PluginLogger
import me.spartacus04.colosseum.scheduler.ColosseumScheduler
import org.bukkit.plugin.Plugin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI

/**
 * A class that handles plugins updates.
 *
 * @property plugin The plugin instance.
 * @property repo The GitHub repository url.
 * @property pluginLogger The plugin logger.
 */
class PluginUpdater(private val plugin: Plugin, private val repo: String, private val pluginLogger: PluginLogger? = null) {
    /**
     * Checks for updates and runs the consumer with the latest version.
     *
     * @param consumer The consumer to run with the latest version.
     */
    fun getVersion(consumer: (String) -> Unit) {
        ColosseumScheduler.getScheduler(plugin).runTaskAsynchronously {
            try {
                val reader = BufferedReader(InputStreamReader(URI("https://api.github.com/repos/$repo/releases/latest").toURL().openStream()))
                val text = reader.use {
                    it.readText()
                }

                val version = Regex("\"tag_name\": ?\"([^\"]+)\"").find(text)?.groupValues?.get(1)!!
                pluginLogger?.debug("Latest version: $version")
                consumer(version)
            } catch (exception: IOException) {
                pluginLogger?.warn("Unable to check for updates: " + exception.message)
            }
        }
    }
}