package me.spartacus04.colosseum.scheduler.schedulersImpl.folia

import me.spartacus04.colosseum.scheduler.scheduling.TaskScheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

/**
 * Wrapper class for Folia's scheduler.
 *
 * @property plugin The plugin instance.
 */
open class FoliaScheduler(private val plugin: Plugin) : TaskScheduler() {
    private val regionScheduler = plugin.server.regionScheduler
    private val globalRegionScheduler = plugin.server.globalRegionScheduler
    private val asyncScheduler = plugin.server.asyncScheduler

    override fun isGlobalThread() = Bukkit.getServer().isGlobalTickThread

    override fun isEntityThread(entity: Entity) = Bukkit.getServer().isOwnedByCurrentRegion(entity)

    override fun isRegionThread(location: Location) = Bukkit.getServer().isOwnedByCurrentRegion(location)

    override fun runTask(runnable: Runnable) = FoliaScheduledTask(globalRegionScheduler.run(plugin, { runnable.run() }))

    override fun runTaskLater(runnable: Runnable, delay: Long): FoliaScheduledTask {
        if (delay <= 0) {
            return runTask(runnable)
        }
        return FoliaScheduledTask(globalRegionScheduler.runDelayed(plugin, { runnable.run() }, delay))
    }

    override fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): FoliaScheduledTask {
        val finalDelay = if (delay <= 0) 1 else delay
        return FoliaScheduledTask(globalRegionScheduler.runAtFixedRate(plugin, { runnable.run() }, finalDelay, period))
    }

    override fun runTask(location: Location, runnable: Runnable) = FoliaScheduledTask(regionScheduler.run(plugin, location) { runnable.run() })

    override fun runTaskLater(location: Location, runnable: Runnable, delay: Long): FoliaScheduledTask {
        if (delay <= 0) {
            return runTask(runnable)
        }
        return FoliaScheduledTask(regionScheduler.runDelayed(plugin, location, { runnable.run() }, delay))
    }

    override fun runTaskTimer(location: Location, runnable: Runnable, delay: Long, period: Long): FoliaScheduledTask {
        val finalDelay = if (delay <= 0) 1 else delay
        return FoliaScheduledTask(regionScheduler.runAtFixedRate(plugin, location, { runnable.run() }, finalDelay, period))
    }

    override fun runTask(entity: Entity, runnable: Runnable) = FoliaScheduledTask(entity.scheduler.run(plugin, { runnable.run() }, null)!!)

    override fun runTaskLater(entity: Entity, runnable: Runnable, delay: Long): FoliaScheduledTask {
        if (delay <= 0) {
            return runTask(entity, runnable)
        }
        return FoliaScheduledTask(entity.scheduler.runDelayed(plugin, { runnable.run() }, null, delay)!!)
    }

    override fun runTaskTimer(entity: Entity, runnable: Runnable, delay: Long, period: Long): FoliaScheduledTask {
        val finalDelay = if (delay <= 0) 1 else delay
        return FoliaScheduledTask(entity.scheduler.runAtFixedRate(plugin, { runnable.run() }, null, finalDelay, period)!!)
    }

    override fun runTaskAsynchronously(runnable: Runnable) = FoliaScheduledTask(asyncScheduler.runNow(plugin) { runnable.run() })

    override fun runTaskLaterAsynchronously(runnable: Runnable, delay: Long): FoliaScheduledTask {
        val finalDelay = if (delay <= 0) 1 else delay
        return FoliaScheduledTask(asyncScheduler.runDelayed(plugin, { runnable.run() }, finalDelay * 50, TimeUnit.MILLISECONDS))
    }

    override fun runTaskTimerAsynchronously(runnable: Runnable, delay: Long, period: Long): FoliaScheduledTask {
        return FoliaScheduledTask(asyncScheduler.runAtFixedRate(plugin, { runnable.run() }, delay * 50, period * 50, TimeUnit.MILLISECONDS))
    }

    override fun execute(runnable: Runnable) { globalRegionScheduler.execute(plugin) { runnable.run() } }

    override fun execute(location: Location, runnable: Runnable) { regionScheduler.execute(plugin, location) { runnable.run() } }

    override fun execute(entity: Entity, runnable: Runnable) { entity.scheduler.execute(plugin, { runnable.run() }, null, 1L) }

    override fun cancelTasks() {
        globalRegionScheduler.cancelTasks(plugin)
        asyncScheduler.cancelTasks(plugin)
    }

    override fun cancelTasks(plugin: Plugin) {
        globalRegionScheduler.cancelTasks(plugin)
        asyncScheduler.cancelTasks(plugin)
    }
}