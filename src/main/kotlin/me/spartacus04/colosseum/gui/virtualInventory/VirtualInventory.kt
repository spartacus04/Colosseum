package me.spartacus04.colosseum.gui.virtualInventory

import me.spartacus04.colosseum.gui.Gui
import me.spartacus04.colosseum.gui.items.AbstractItemProvider
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class VirtualInventory(val size: Int, val itemStacks: Array<ItemStack?> = arrayOfNulls(size)) {
    private val registeredGuis = mutableListOf<Pair<Gui, Int>>()

    fun unRegisterGui(gui: Gui) {
        registeredGuis.removeAll { it.first == gui }
    }

    fun registerGui(gui: Gui, inventoryId: Int) {
        registeredGuis.add(gui to inventoryId)
    }

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

    fun refreshAll() {
        registeredGuis.forEach { (gui, inventoryId) ->
            gui.refreshVirtualInventory(inventoryId)
        }
    }

    fun getItem(slot: Int): ItemStack? {
        return itemStacks.getOrNull(slot)?.clone()
    }

    operator fun get(slot: Int): ItemStack? = getItem(slot)

    operator fun set(slot: Int, itemStack: ItemStack?) = setItem(slot, itemStack)

    fun handleClick(clickEvent: VirtualInventoryInteractEvent) {
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

    open fun onPreUpdateEvent(event: VirtualInventoryInteractEvent) = Unit

    open fun onPostUpdateEvent(event: VirtualInventoryInteractEvent) = Unit

    class VirtualInventoryItemProvider(val virtualInventory: VirtualInventory, slot: Int) : AbstractItemProvider() {
        var slot: Int = slot
            set(value) {
                field = value
                refresh()
            }

        override fun getItem(): ItemStack? {
            return if(slot !in 0..<virtualInventory.size) null else virtualInventory.getItem(slot)
        }

        override fun onClick(clickEvent: InventoryClickEvent) = Unit
    }
}