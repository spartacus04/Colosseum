package me.spartacus04.colosseum.gui.virtualInventory

import me.spartacus04.colosseum.gui.Gui
import me.spartacus04.colosseum.gui.items.AbstractItemProvider
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * A VirtualInventory is an inventory that is not directly tied to a physical inventory in the game, but can be used to represent a custom inventory for a GUI. It allows you to manage the items in the inventory and handle interactions with it without needing to create an actual Inventory object in Bukkit.
 *
 * This can be useful for GUIs that have dynamic content or need to manage multiple inventories at once, as it allows you to keep track of the inventory state and update it as needed without having to worry about the underlying Bukkit inventory mechanics.
 *
 * The VirtualInventory class provides methods for setting and getting items in the inventory, as well as registering GUIs that are using the inventory so that they can be updated when changes are made. It also includes a method for handling click events on the inventory, which can be overridden to implement custom behavior when items are interacted with.
 *
 * @param size The size of the virtual inventory, which determines how many slots it has. This should be a positive integer and is typically a multiple of 9 to fit standard Minecraft inventory layouts.
 * @param itemStacks An optional array of ItemStacks to initialize the inventory with. If no array is provided, the inventory will be initialized with null values (empty slots). The length of this array should match the specified size of the inventory.
 */
abstract class VirtualInventory(val size: Int, val itemStacks: Array<ItemStack?> = arrayOfNulls(size)) {
    private val registeredGuis = mutableListOf<Pair<Gui, Int>>()

    /**
     * Unregisters a GUI from this virtual inventory, so that it will no longer receive updates when the inventory changes. This is useful for cleaning up references to GUIs that are no longer in use or have been closed, preventing memory leaks and ensuring that only active GUIs are updated with changes to the inventory.
     *
     * @param gui The GUI to unregister from this virtual inventory. This should be the same instance of the GUI that was previously registered with the registerGui method.
     */
    fun unRegisterGui(gui: Gui) {
        registeredGuis.removeAll { it.first == gui }
    }

    /**
     * Registers a GUI to receive updates from this virtual inventory. When a GUI is registered, it will be notified of any changes to the inventory and can update its display accordingly. The inventoryId parameter is used to identify which virtual inventory the GUI is associated with, allowing for multiple virtual inventories to be managed by the same GUI if needed.
     *
     * @param gui The GUI to register with this virtual inventory.
     * @param inventoryId An integer identifier for the virtual inventory, used to associate the GUI with this specific inventory.
     */
    fun registerGui(gui: Gui, inventoryId: Int) {
        registeredGuis.add(gui to inventoryId)
    }

    /**
     * Sets the item in the specified slot of the virtual inventory. If the provided itemStack is null, has an amount of 0, or is of type AIR, the slot will be set to null (empty). After updating the item in the inventory, this method will notify all registered GUIs to refresh the corresponding slot in their display, ensuring that any changes to the inventory are reflected in the GUI.
     *
     * @param slot The index of the slot to update, which should be between 0 and size-1. If the slot index is out of bounds, the method will return without making any changes.
     * @param itemStack The ItemStack to set in the specified slot. If this is null, has an amount of 0, or is of type AIR, the slot will be cleared (set to null). Otherwise, the provided ItemStack will be set in the inventory at the specified slot.
     */
    fun setItem(slot: Int, itemStack: ItemStack?) {
        if (slot !in 0..<size) return
        val stack = if(itemStack != null && (
            itemStack.amount == 0 || itemStack.type.isAir
        )) null else itemStack

        itemStacks[slot] = stack

        registeredGuis.forEach {
            it.first.refreshVirtualInventorySlot(it.second, slot)
        }
    }

    /**
     * Refreshes all registered GUIs that are associated with this virtual inventory, prompting them to update their display to reflect the current state of the inventory. This method can be called after making multiple changes to the inventory to ensure that all GUIs are updated at once, rather than refreshing each GUI individually for every change. It iterates through all registered GUIs and calls their refreshVirtualInventory method with the corresponding inventoryId, allowing them to update their display based on the latest state of the virtual inventory.
     */
    fun refreshAll() {
        registeredGuis.forEach { (gui, inventoryId) ->
            gui.refreshVirtualInventory(inventoryId)
        }
    }

    /**
     * Retrieves the item in the specified slot of the virtual inventory. If the slot index is out of bounds (not between 0 and size-1), this method will return null. Otherwise, it returns a clone of the ItemStack at the specified slot, allowing callers to safely modify the returned ItemStack without affecting the inventory's internal state. If the slot is empty (null), this method will also return null.
     *
     * @param slot The index of the slot to retrieve, which should be between 0 and size-1. If the slot index is out of bounds, the method will return null.
     */
    fun getItem(slot: Int): ItemStack? {
        return itemStacks.getOrNull(slot)?.clone()
    }

    /**
     * Allows for array-like access to the virtual inventory's items using the bracket notation. This operator function delegates to the getItem method, enabling you to retrieve items from the inventory using syntax like virtualInventory[slot]. If the slot index is out of bounds or the slot is empty, this will return null. This provides a convenient way to access items in the virtual inventory without needing to call getItem directly.
     *
     * @param slot The index of the slot to retrieve, which should be between 0 and size-1. If the slot index is out of bounds or the slot is empty, this will return null.
     */
    operator fun get(slot: Int): ItemStack? = getItem(slot)

    /**
     * Allows for array-like assignment to the virtual inventory's items using the bracket notation. This operator function delegates to the setItem method, enabling you to set items in the inventory using syntax like virtualInventory[slot] = itemStack. If the provided itemStack is null, has an amount of 0, or is of type AIR, the specified slot will be cleared (set to null). Otherwise, the provided ItemStack will be set in the inventory at the specified slot. This provides a convenient way to modify items in the virtual inventory without needing to call setItem directly.
     *
     * @param slot The index of the slot to update, which should be between 0 and size-1. If the slot index is out of bounds, the method will return without making any changes.
     * @param itemStack The ItemStack to set in the specified slot. If this is null, has an amount of 0, or is of type AIR, the slot will be cleared (set to null). Otherwise, the provided ItemStack will be set in the inventory at the specified slot.
     */
    operator fun set(slot: Int, itemStack: ItemStack?) = setItem(slot, itemStack)

    /**
     * Handles click events on the virtual inventory. This method should be called when a click event occurs in a GUI that is associated with this virtual inventory. It checks if any of the slot changes in the click event are outside the bounds of the inventory size, and if so, it cancels the event to prevent invalid interactions. If the click event is valid, it calls the onPreUpdateEvent method to allow for any necessary processing before updating the inventory. Then, it schedules a task to run shortly after the click event to update the inventory based on the slot changes, setting the items in the virtual inventory to match the items in the physical inventory at the corresponding slots. Finally, it calls the onPostUpdateEvent method after updating the inventory to allow for any additional processing or cleanup after the update is complete.
     *
     * This method is designed to be called from the GUI's click event handler, allowing the virtual inventory to manage its state and ensure that any interactions with it are properly handled and reflected in the associated GUIs.
     *
     * @param clickEvent The VirtualInventoryInteractEvent that contains information about the click event, including the slot changes and the GUI that was interacted with. This event is used to determine how to update the virtual inventory based on the user's interaction with the GUI.
     */
    open fun handleClick(clickEvent: VirtualInventoryInteractEvent) {
        if(clickEvent.slotChanges.any {
                it.virtualSlot !in 0..<size
            }) {
            clickEvent.isCancelled = true
            return
        }

        onPreUpdateEvent(clickEvent)

        clickEvent.plugin.scheduler.runTaskLater({
            if (!clickEvent.isCancelled) {
                clickEvent.slotChanges.forEach {
                    setItem(
                        it.virtualSlot,
                        clickEvent.gui.inventory.getItem(it.physicalSlot)
                    )
                }
            }

            onPostUpdateEvent(clickEvent)
        }, 1L)
    }

    /**
     * Called before the virtual inventory is updated in response to a click event. This method can be overridden to perform any necessary processing or validation before the inventory is modified based on the user's interaction. For example, you could use this method to check for specific conditions, modify the click event, or cancel the event if certain criteria are not met. By default, this method does nothing, but it provides a hook for subclasses to implement custom behavior before the inventory update occurs.
     *
     * @param event The VirtualInventoryInteractEvent that triggered the update, containing information about the click event and the GUI interaction. This event can be used to access details about the click and make decisions based on that information before the inventory is updated.
     */
    open fun onPreUpdateEvent(event: VirtualInventoryInteractEvent) = Unit

    /**
     * Called after the virtual inventory has been updated in response to a click event. This method can be overridden to perform any necessary processing or cleanup after the inventory has been modified based on the user's interaction. For example, you could use this method to trigger additional updates, log changes, or perform any actions that should occur after the inventory state has been changed. By default, this method does nothing, but it provides a hook for subclasses to implement custom behavior after the inventory update is complete.
     *
     * @param event The VirtualInventoryInteractEvent that triggered the update, containing information about the click event and the GUI interaction. This event can be used to access details about the click and perform actions based on that information after the inventory has been updated.
     */
    open fun onPostUpdateEvent(event: VirtualInventoryInteractEvent) = Unit

    /**
     * An ItemProvider that provides the item from a specific slot in the virtual inventory. This can be used to create dynamic GUI elements that reflect the current state of the virtual inventory, allowing you to display items from the inventory in the GUI and have them update automatically when the inventory changes. The slot property can be updated to change which slot's item is being provided, and the provider will refresh to reflect the new slot's item. When clicked, this provider does nothing by default, but it can be overridden to implement custom behavior when the item is interacted with in the GUI.
      *
      * @param virtualInventory The VirtualInventory instance that this provider will retrieve items from. This should be the same instance of the virtual inventory that is being used in the associated GUI.
      * @param slot The initial slot index that this provider will retrieve items from. This should be between 0 and size-1 of the virtual inventory. You can change this slot later by setting the slot property.
     */
    class VirtualInventoryItemProvider(val virtualInventory: VirtualInventory, slot: Int) : AbstractItemProvider() {
        /**
         * The slot index that this provider retrieves items from in the virtual inventory. This should be between 0 and size-1 of the virtual inventory. When this property is updated, the provider will refresh to reflect the item from the new slot. You can change this slot at any time to have the provider retrieve items from a different slot in the virtual inventory.
         *
         * When the slot is updated, the provider will call refresh() to ensure that any GUIs using this provider will update their display to show the item from the new slot. This allows for dynamic behavior where the provider can switch between different slots in the virtual inventory and have the GUI reflect those changes automatically.
         */
        var slot: Int = slot
            set(value) {
                field = value
                refresh()
            }

        /**
         * Retrieves the item from the specified slot in the virtual inventory. If the slot index is out of bounds (not between 0 and size-1), this method will return null. Otherwise, it returns a clone of the ItemStack at the specified slot in the virtual inventory, allowing callers to safely modify the returned ItemStack without affecting the inventory's internal state. If the slot is empty (null), this method will also return null.
         */
        override fun getItem(): ItemStack? {
            return if(slot !in 0..<virtualInventory.size) null else virtualInventory.getItem(slot)
        }

        override fun onClick(clickEvent: InventoryClickEvent) = Unit
    }
}