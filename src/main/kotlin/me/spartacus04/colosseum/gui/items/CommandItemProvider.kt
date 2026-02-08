package me.spartacus04.colosseum.gui.items

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * An ItemProvider that executes a command when clicked. The command will be executed as if the player had typed it in chat, so it can include placeholders like %player% that will be replaced with the player's name.
 *
 * @param itemStack The item to display in the GUI.
 * @param command The command to execute when the item is clicked. This can include placeholders like
 */
class CommandItemProvider(val itemStack: ItemStack, val command: String) : AbstractItemProvider() {
    override fun getItem() = itemStack

    override fun onClick(clickEvent: InventoryClickEvent) {
        val player = clickEvent.whoClicked
        Bukkit.dispatchCommand(player, command)
    }
}