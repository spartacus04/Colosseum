package me.spartacus04.colosseum.scheduler.schedulersImpl.folia

import me.spartacus04.colosseum.scheduler.scheduling.ScheduledTask

/**
 * Represents a task that has been scheduled using Folia's scheduler.
 *
 * @property task The Folia task.
 */
class FoliaScheduledTask(private val task: io.papermc.paper.threadedregions.scheduler.ScheduledTask) : ScheduledTask {
    override fun cancel() { task.cancel() }

    override fun isCancelled() = task.isCancelled

    override fun getOwningPlugin() = task.owningPlugin

    override fun isCurrentlyRunning(): Boolean {
        val state = task.executionState
        return state == io.papermc.paper.threadedregions.scheduler.ScheduledTask.ExecutionState.RUNNING || state == io.papermc.paper.threadedregions.scheduler.ScheduledTask.ExecutionState.CANCELLED_RUNNING
    }

    override fun isRepeatingTask() = task.isRepeatingTask
}