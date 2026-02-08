package me.spartacus04.colosseum.gui.items

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CommandItemProvider(val itemStack: ItemStack, val command: String) : AbstractItemProvider() {
    override fun getItem() = itemStack

    override fun onClick(clickEvent: InventoryClickEvent) {
        val player = clickEvent.whoClicked
        Bukkit.dispatchCommand(player, command)
    }
}