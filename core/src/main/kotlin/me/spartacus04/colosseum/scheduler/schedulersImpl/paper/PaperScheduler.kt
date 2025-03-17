package me.spartacus04.colosseum.scheduler.schedulersImpl.paper

import me.spartacus04.colosseum.scheduler.schedulersImpl.folia.FoliaScheduler
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

/**
 * Wrapper class for Paper's scheduler.
 *
 * @property plugin The plugin instance.
 */
class PaperScheduler(plugin: Plugin) : FoliaScheduler(plugin) {
    override fun isGlobalThread() = Bukkit.getServer().isPrimaryThread
}