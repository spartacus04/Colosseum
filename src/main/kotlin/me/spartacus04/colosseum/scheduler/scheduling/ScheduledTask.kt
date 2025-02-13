package me.spartacus04.colosseum.scheduler.scheduling

import org.bukkit.plugin.Plugin

/**
 * Represents a task that has been scheduled.
 */
interface ScheduledTask {

    /**
     * Cancels the task.
     */
    fun cancel(): Unit

    /**
     * @return Whether the task has been cancelled.
     */
    fun isCancelled(): Boolean

    /**
     * @return The plugin that owns this task.
     */
    fun getOwningPlugin(): Plugin

    /**
     * @return Whether the task is currently running.
     */
    fun isCurrentlyRunning(): Boolean

    /**
     * @return Whether the task is a repeating task.
     */
    fun isRepeatingTask(): Boolean
}