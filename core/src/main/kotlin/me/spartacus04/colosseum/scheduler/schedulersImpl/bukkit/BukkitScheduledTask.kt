package me.spartacus04.colosseum.scheduler.schedulersImpl.bukkit

import me.spartacus04.colosseum.scheduler.scheduling.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

/**
 * Represents a task that has been scheduled using Bukkit's scheduler.
 *
 * @property task The Bukkit task.
 * @property isRepeating Whether the task is repeating.
 */
class BukkitScheduledTask(private val task: BukkitTask, private val isRepeating: Boolean = false) : ScheduledTask {
    override fun cancel() = task.cancel()

    override fun isCancelled() = task.isCancelled

    override fun getOwningPlugin() = task.owner

    override fun isCurrentlyRunning() =
        Bukkit.getServer().scheduler.isCurrentlyRunning(task.taskId)

    override fun isRepeatingTask() = isRepeating
}