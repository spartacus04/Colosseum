package me.spartacus04.colosseum.gui.items

import org.bukkit.event.inventory.InventoryClickEvent

abstract class ScrollItemProvider(val scrollAmount: Int, val inventoryId: Int) : AbstractItemProvider() {
    override fun onClick(clickEvent: InventoryClickEvent) {
        this.gui.handleInventoryScroll(inventoryId, scrollAmount)
    }
}