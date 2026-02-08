package me.spartacus04.colosseum.gui.items

import org.bukkit.event.inventory.InventoryClickEvent

/**
 * An ItemProvider that scrolls a VirtualInventory when clicked. This can be used for things like next/previous page buttons in a paginated GUI.
 *
 * @param scrollAmount The amount to scroll the inventory by when clicked. Positive values will scroll forward, negative values will scroll backward.
 * @param inventoryId The ID of the inventory to scroll. This allows the provider to specify
 */
abstract class ScrollItemProvider(val scrollAmount: Int, val inventoryId: Int) : AbstractItemProvider() {
    override fun onClick(clickEvent: InventoryClickEvent) {
        this.gui.handleInventoryScroll(inventoryId, scrollAmount)
    }
}