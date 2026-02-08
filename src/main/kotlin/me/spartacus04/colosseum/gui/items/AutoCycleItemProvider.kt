package me.spartacus04.colosseum.gui.items

import me.spartacus04.colosseum.ColosseumPlugin
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class AutoCycleItemProvider(private val items: List<ItemStack>, val plugin: ColosseumPlugin, cycleInterval: Long = 1L) : AbstractItemProvider() {
    private var currentIndex = 0
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