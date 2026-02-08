package me.spartacus04.colosseum.gui.items

import me.spartacus04.colosseum.ColosseumPlugin
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * An ItemProvider that automatically cycles through a list of items at a specified interval.
 * This is useful for creating dynamic GUI elements that change over time without user interaction.
 *
 * @param items The list of items to cycle through. The provider will display one item at a time, cycling through the list in order.
 * @param plugin The instance of the ColosseumPlugin, used to schedule the cycling task
 * @param cycleInterval The interval in ticks at which to cycle to the next item. Defaults to 1 tick (20 cycles per second).
 */
class AutoCycleItemProvider(private val items: List<ItemStack>, val plugin: ColosseumPlugin, cycleInterval: Long = 1L) : AbstractItemProvider() {
    /**
     * The index of the currently displayed item. This is updated by the scheduled task to cycle through the items list.
     */
    private var currentIndex = 0

    /**
     * A scheduled task that runs every [cycleInterval] ticks to update the [currentIndex] and refresh the displayed item. This ensures that the item cycles automatically without user interaction.
     */
    private val task = plugin.scheduler.runTaskTimer({
        currentIndex = (currentIndex + 1) % items.size
        refresh()
    }, cycleInterval, cycleInterval)

    override fun getItem(): ItemStack? {
        if(items.isEmpty()) return null
        return items[currentIndex]
    }

    override fun onClick(clickEvent: InventoryClickEvent) = Unit

    override fun destroy() {
        task.cancel()
    }
}