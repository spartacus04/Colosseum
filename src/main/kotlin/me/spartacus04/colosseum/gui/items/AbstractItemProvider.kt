package me.spartacus04.colosseum.gui.items

import me.spartacus04.colosseum.gui.Gui
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Base class for item providers in the GUI system.
 * Provides common functionality for managing the GUI reference and inventory slot.
 */
abstract class AbstractItemProvider {

    /**
     * Reference to the parent GUI. This will be set when the item provider is added to a GUI.
     */
    protected lateinit var gui: Gui

    /**
     * The inventory slot index where this item provider is located. This will be set when the item provider is added to a GUI.
     */
    protected var inventorySlot: Int = -1

    /**
     * Sets the GUI reference and inventory slot for this item provider. This should be called by the GUI when adding this item provider to it.
     *
     * @param gui The parent GUI that this item provider belongs to.
     * @param slot The inventory slot index where this item provider is located.
     */
    fun setGuiData(gui: Gui, slot: Int) {
        this.gui = gui
        this.inventorySlot = slot
    }

    /**
     * Returns the ItemStack to be displayed for this item provider. This method must be implemented by subclasses to provide the actual item to display.
     */
    abstract fun getItem(): ItemStack?

    /**
     * Handles click events for this item provider. This method must be implemented by subclasses to define the behavior when the item is clicked in the GUI.
     *
     * @param clickEvent The InventoryClickEvent that occurred when the item was clicked.
     */
    abstract fun onClick(clickEvent: InventoryClickEvent)

    /**
     * Refreshes the item in the GUI. This method can be called by subclasses to update the displayed item after changes.
     * It checks if the GUI reference is initialized and if the inventory slot is valid before attempting to refresh.
     */
    fun refresh() {
        if(this::gui.isInitialized && inventorySlot != -1) {
            gui.refreshIndex(inventorySlot)
        }
    }

    /**
     * Cleans up any resources or references when this item provider is removed from the GUI.
     * Subclasses can override this method to perform any necessary cleanup, such as canceling tasks or clearing references.
     */
    open fun destroy() = Unit
}