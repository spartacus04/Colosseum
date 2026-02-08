package me.spartacus04.colosseum.gui.items

import org.bukkit.inventory.ItemStack

/**
 * An ItemProvider that cycles through a list of items each time it is clicked. This can be used for things like toggles or settings that have multiple options.
 *
 * @param items The list of items to cycle through. The first item in the list will be displayed initially.
 */
class CycleItemProvider(private val items: List<ItemStack>): AbstractItemProvider() {
    private var currentIndex = 0

    override fun getItem(): ItemStack? {
        if(items.isEmpty()) return null
        return items[currentIndex]
    }

    override fun onClick(clickEvent: org.bukkit.event.inventory.InventoryClickEvent) {
        currentIndex = (currentIndex + 1) % items.size
        refresh()
    }
}