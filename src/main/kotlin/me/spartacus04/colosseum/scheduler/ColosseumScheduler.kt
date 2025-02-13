package me.spartacus04.colosseum.scheduler

import me.spartacus04.colosseum.scheduler.schedulersImpl.bukkit.BukkitScheduler
import me.spartacus04.colosseum.scheduler.schedulersImpl.folia.FoliaScheduler
import me.spartacus04.colosseum.scheduler.schedulersImpl.paper.PaperScheduler
import me.spartacus04.colosseum.scheduler.scheduling.TaskScheduler
import org.bukkit.plugin.Plugin

/**
 * A utility class to get the appropriate scheduler based on the server implementation.
 */
object ColosseumScheduler {
    private val isFolia =
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
            true
        } catch (_: ClassNotFoundException) {
            false
        }

    private val isExpandedSchedulingAvailable =
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask")
            true
        } catch (_: ClassNotFoundException) {
            false
        }

    fun getScheduler(plugin: Plugin): TaskScheduler {
        return if (isFolia) {
            FoliaScheduler(plugin)
        } else if (isExpandedSchedulingAvailable) {
            PaperScheduler(plugin)
        } else {
            BukkitScheduler(plugin)
        }
    }
}