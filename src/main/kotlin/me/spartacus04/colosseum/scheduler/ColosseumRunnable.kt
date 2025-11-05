package me.spartacus04.colosseum.scheduler

import me.spartacus04.colosseum.scheduler.scheduling.ScheduledTask
import org.bukkit.plugin.Plugin
import kotlin.jvm.Throws

/**
 * Represents a task that can be scheduled.
 */
abstract class ColosseumRunnable : Runnable {
    private var task: ScheduledTask? = null

    /**
     * Sets up the scheduled task.
     *
     * @param task the scheduled task
     * @return the scheduled task
     */
    private fun setupTask(task: ScheduledTask): ScheduledTask {
        this.task = task
        return task
    }

    /**
     * Checks if the task has been scheduled.
     *
     * @throws IllegalStateException if the task was not scheduled yet
     */
    private fun checkScheduled() {
        if (task == null) {
            throw IllegalStateException("Not scheduled yet")
        }
    }

    /**
     * Checks if the task has not been scheduled yet.
     *
     * @throws IllegalStateException if the task was already scheduled
     */
    private fun checkNotYetScheduled() {
        if (task != null) {
            throw IllegalStateException("Already scheduled")
        }
    }


    /**
     * Cancels this task.
     *
     * @throws IllegalStateException if this was not scheduled yet
     */
    @Synchronized
    @Throws(IllegalStateException::class)
    fun cancel() {
        checkScheduled()
        task!!.cancel()
    }

    /**
     * Returns true if this task has been cancelled.
     *
     * @return true if the task has been cancelled
     * @throws IllegalStateException if task was not scheduled yet
     */
    @Synchronized
    @Throws(IllegalStateException::class)
    fun isCancelled(): Boolean {
        checkScheduled()
        return task!!.isCancelled()
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @param plugin the reference to the plugin scheduling task
     * @return {@link MyScheduledTask}
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTask(plugin: Plugin): ScheduledTask {
        checkNotYetScheduled()
        return setupTask(ColosseumScheduler.getScheduler(plugin).runTask(this))
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks. <br>
     * Schedules this in the Bukkit scheduler to run asynchronously.
     *
     * @param plugin the reference to the plugin scheduling task
     * @return {@link MyScheduledTask}
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskAsynchronously(plugin: Plugin): ScheduledTask {
        checkNotYetScheduled()
        return setupTask(ColosseumScheduler.getScheduler(plugin).runTaskAsynchronously(this))
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay  the ticks to wait before running the task
     * @return {@link MyScheduledTask}
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskLater(plugin: Plugin, delay: Long): ScheduledTask {
        checkNotYetScheduled()
        return setupTask(ColosseumScheduler.getScheduler(plugin).runTaskLater(this, delay))
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks. <br>
     * Schedules this to run asynchronously after the specified number of
     * server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay  the ticks to wait before running the task
     * @return {@link MyScheduledTask}
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskLaterAsynchronously(plugin: Plugin, delay: Long): ScheduledTask {
        checkNotYetScheduled()
        return setupTask(ColosseumScheduler.getScheduler(plugin).runTaskLaterAsynchronously(this, delay))
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay  the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return {@link MyScheduledTask}
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskTimer(plugin: Plugin, delay: Long, period: Long): ScheduledTask {
        checkNotYetScheduled()
        return setupTask(ColosseumScheduler.getScheduler(plugin).runTaskTimer(this, delay, period))
    }

    /**
     * Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks. <br>
     * Schedules this to repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay  the ticks to wait before running the task for the first
     *               time
     * @param period the ticks to wait between runs
     * @return {@link MyScheduledTask}
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun runTaskTimerAsynchronously(plugin: Plugin, delay: Long, period: Long): ScheduledTask {
        checkNotYetScheduled()
        return setupTask(ColosseumScheduler.getScheduler(plugin).runTaskTimerAsynchronously(this, delay, period))
    }
}