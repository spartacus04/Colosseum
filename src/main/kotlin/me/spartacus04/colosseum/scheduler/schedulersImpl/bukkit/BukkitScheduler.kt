package me.spartacus04.colosseum.scheduler.schedulersImpl.bukkit

import me.spartacus04.colosseum.scheduler.scheduling.TaskScheduler
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import org.bukkit.entity.Entity

/**
 * Wrapper class for Bukkit's scheduler.
 *
 * @property plugin The plugin instance.
 */
class BukkitScheduler(private val plugin: Plugin) : TaskScheduler() {
    override fun isGlobalThread() = plugin.server.isPrimaryThread

    override fun isEntityThread(entity: Entity) = plugin.server.isPrimaryThread

    override fun isRegionThread(location: Location) = plugin.server.isPrimaryThread

    override fun runTask(runnable: Runnable) = BukkitScheduledTask(plugin.server.scheduler.runTask(plugin, runnable))

    override fun runTaskLater(runnable: Runnable, delay: Long) = BukkitScheduledTask(plugin.server.scheduler.runTaskLater(plugin, runnable, delay))

    override fun runTaskTimer(runnable: Runnable, delay: Long, period: Long) = BukkitScheduledTask(plugin.server.scheduler.runTaskTimer(plugin, runnable, delay, period))

    override fun runTaskAsynchronously(runnable: Runnable) = BukkitScheduledTask(plugin.server.scheduler.runTaskAsynchronously(plugin, runnable))

    override fun runTaskLaterAsynchronously(runnable: Runnable, delay: Long) = BukkitScheduledTask(plugin.server.scheduler.runTaskLaterAsynchronously(plugin, runnable, delay))

    override fun runTaskTimerAsynchronously(runnable: Runnable, delay: Long, period: Long) = BukkitScheduledTask(plugin.server.scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period))

    override fun execute(runnable: Runnable) { plugin.server.scheduler.scheduleSyncDelayedTask(plugin, runnable) }

    override fun cancelTasks() = plugin.server.scheduler.cancelTasks(plugin)

    override fun cancelTasks(plugin: Plugin) = plugin.server.scheduler.cancelTasks(plugin)
}