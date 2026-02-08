package me.spartacus04.colosseum.gui.items

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * A simple ItemProvider that always returns the same item and does nothing when clicked. This can be used for static items in the GUI that don't need to do anything when clicked, such as decorative items or placeholders.
 * @param item The item to display in the GUI. This item will be returned by the getItem() method and will not change unless the provider is replaced with a different one.
 */
class SimpleItemProvider(private val item: ItemStack) : AbstractItemProvider() {
    override fun getItem() = item

    override fun onClick(
        clickEvent: InventoryClickEvent
    ) = Unit
}