package me.spartacus04.colosseum.scheduler.scheduling

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.CompletableFuture

/**
 * Represents a generic task scheduler.
 */
abstract class TaskScheduler {
    /**
     * Folia: Returns whether the current thread is ticking the global region <br>
     * Paper & Bukkit: Returns {@link org.bukkit.Server#isPrimaryThread}
     */
    abstract fun isGlobalThread(): Boolean

    /**
     * @return {@link org.bukkit.Server#isPrimaryThread}
     */
    open fun isTickThread() = Bukkit.getServer().isPrimaryThread

    /**
     * Folia & Paper: Returns whether the current thread is ticking a region and that the region
     * being ticked owns the specified entity. Note that this function is the only appropriate method of
     * checking for ownership of an entity, as retrieving the entity's location is undefined unless the
     * entity is owned by the current region <br>
     * Bukkit: returns {@link org.bukkit.Server#isPrimaryThread}
     */
    abstract fun isEntityThread(entity: Entity): Boolean

    /**
     * Folia & Paper: Returns whether the current thread is ticking a region and that the region
     * being ticked owns the chunk at the specified world and block position as included in the specified location <br>
     * Bukkit: returns {@link org.bukkit.Server#isPrimaryThread}
     */
    abstract fun isRegionThread(location: Location): Boolean

    /**
     * Schedules a task to be executed on the next tick <br>
     * Folia & Paper: ...on the global region <br>
     * Bukkit: ...on the main thread
     *
     * @param runnable The task to execute
     */
    abstract fun runTask(runnable: Runnable): ScheduledTask

    /**
     * Schedules a task to be executed after the specified delay in ticks <br>
     * Folia & Paper: ...on the global region <br>
     * Bukkit: ...on the main thread
     *
     * @param runnable The task to execute
     * @param delay    The delay, in ticks
     */
    abstract fun runTaskLater(runnable: Runnable, delay: Long): ScheduledTask

    /**
     * Schedules a repeating task to be executed after the initial delay with the specified period <br>
     * Folia & Paper: ...on the global region <br>
     * Bukkit: ...on the main thread
     *
     * @param runnable The task to execute
     * @param delay    The initial delay, in ticks.
     * @param period   The period, in ticks.
     */
    abstract fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): ScheduledTask

    /**
     * Folia & Paper: Schedules a task to be executed on the region which owns the location on the next tick <br>
     * Bukkit: same as {@link #runTask(Runnable)}
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     */
    open fun runTask(location: Location, runnable: Runnable): ScheduledTask {
        return runTask(runnable)
    }

    /**
     * Folia & Paper: Schedules a task to be executed on the region which owns the location after the specified delay in ticks <br>
     * Bukkit: same as {@link #runTaskLater(Runnable, long)}
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     * @param delay    The delay, in ticks.
     */
    open fun runTaskLater(location: Location, runnable: Runnable, delay: Long): ScheduledTask {
        return runTaskLater(runnable, delay)
    }

    /**
     * Folia & Paper: Schedules a repeating task to be executed on the region which owns the location after the initial delay with the specified period <br>
     * Bukkit: same as {@link #runTaskTimer(Runnable, long, long)}
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     * @param delay    The initial delay, in ticks.
     * @param period   The period, in ticks.
     */
    open fun runTaskTimer(location: Location, runnable: Runnable, delay: Long, period: Long): ScheduledTask {
        return runTaskTimer(runnable, delay, period)
    }

    /**
     * Folia & Paper: Schedules a task to be executed on the region which owns the location of given entity on the next tick <br>
     * Bukkit: same as {@link #runTask(Runnable)}
     *
     * @param entity   The entity whose location the region executing should own
     * @param runnable The task to execute
     */
    open fun runTask(entity: Entity, runnable: Runnable): ScheduledTask {
        return runTask(runnable)
    }

    /**
     * Folia & Paper: Schedules a task to be executed on the region which owns the location of given entity after the specified delay in ticks <br>
     * Bukkit: same as {@link #runTaskLater(Runnable, long)}
     *
     * @param entity   The entity whose location the region executing should own
     * @param runnable The task to execute
     * @param delay    The delay, in ticks.
     */
    open fun runTaskLater(entity: Entity, runnable: Runnable, delay: Long): ScheduledTask {
        return runTaskLater(runnable, delay)
    }

    /**
     * Folia & Paper: Schedules a repeating task to be executed on the region which owns the location of given entity after the initial delay with the specified period <br>
     * Bukkit: same as {@link #runTaskTimer(Runnable, long, long)}
     *
     * @param entity   The entity whose location the region executing should own
     * @param runnable The task to execute
     * @param delay    The initial delay, in ticks.
     * @param period   The period, in ticks.
     */
    open fun runTaskTimer(entity: Entity, runnable: Runnable, delay: Long, period: Long): ScheduledTask {
        return runTaskTimer(runnable, delay, period)
    }

    /**
     * Schedules the specified task to be executed asynchronously immediately
     *
     * @param runnable The task to execute
     * @return The {@link MyScheduledTask} that represents the scheduled task
     */
    abstract fun runTaskAsynchronously(runnable: Runnable): ScheduledTask

    /**
     * Schedules the specified task to be executed asynchronously after the time delay has passed
     *
     * @param runnable The task to execute
     * @param delay    The time delay to pass before the task should be executed
     * @return The {@link MyScheduledTask} that represents the scheduled task
     */
    abstract fun runTaskLaterAsynchronously(runnable: Runnable, delay: Long): ScheduledTask

    /**
     * Schedules the specified task to be executed asynchronously after the initial delay has passed and then periodically executed with the specified period
     *
     * @param runnable The task to execute
     * @param delay The time delay to pass before the first execution of the task, in ticks
     * @param period The time between task executions after the first execution of the task, in ticks
     * @return The {@link MyScheduledTask} that represents the scheduled task
     */
    abstract fun runTaskTimerAsynchronously(runnable: Runnable, delay: Long, period: Long): ScheduledTask


    /**
     * Calls a method on the main thread and returns a Future object. This task will be executed by the main(Bukkit)/global(Folia&Paper) server thread. <br>
     * Note: The Future.get() methods must NOT be called from the main thread. <br>
     * Note2: There is at least an average of 10ms latency until the isDone() method returns true.
     *
     * @param task Task to be executed
     */
    fun <T>callSyncMethod(task: Callable<T>): Future<T> {
        val completableFuture = CompletableFuture<T>()
        execute {
            try {
                completableFuture.complete(task.call())
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        return completableFuture
    }

    /**
     * Schedules a task to be executed on the global region
     *
     * @param runnable The task to execute
     */
    abstract fun execute(runnable: Runnable)

    /**
     * Schedules a task to be executed on the region which owns the location
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     */
    open fun execute(location: Location, runnable: Runnable) {
        execute(runnable)
    }

    /**
     * Schedules a task to be executed on the region which owns the location of given entity
     *
     * @param entity The entity which location the region executing should own
     * @param runnable The task to execute
     */
    open fun execute(entity: Entity, runnable: Runnable) {
        execute(runnable)
    }

    /**
     * Attempts to cancel all tasks scheduled by this plugin
     */
    abstract fun cancelTasks()

    /**
     * Attempts to cancel all tasks scheduled by the specified plugin
     *
     * @param plugin specified plugin
     */
    abstract fun cancelTasks(plugin: Plugin)
}