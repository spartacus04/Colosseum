package me.spartacus04.colosseum.gui.items

import me.spartacus04.colosseum.gui.Gui
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class AbstractItemProvider {
    protected lateinit var gui: Gui
    protected var inventorySlot: Int = -1

    fun setGuiData(gui: Gui, slot: Int) {
        this.gui = gui
        this.inventorySlot = slot
    }

    abstract fun getItem(): ItemStack?

    abstract fun onClick(clickEvent: InventoryClickEvent)

    fun refresh() {
        if(this::gui.isInitialized && inventorySlot != -1) {
            gui.refreshIndex(inventorySlot)
        }
    }

    open fun destroy() = Unit
}