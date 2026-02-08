package me.spartacus04.colosseum.gui.items

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class SimpleItemProvider(private val item: ItemStack) : AbstractItemProvider() {
    override fun getItem() = item

    override fun onClick(
        clickEvent: InventoryClickEvent
    ) = Unit
}