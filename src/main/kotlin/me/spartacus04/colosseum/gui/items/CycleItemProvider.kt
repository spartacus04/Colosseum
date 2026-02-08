package me.spartacus04.colosseum.gui.items

import org.bukkit.inventory.ItemStack

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