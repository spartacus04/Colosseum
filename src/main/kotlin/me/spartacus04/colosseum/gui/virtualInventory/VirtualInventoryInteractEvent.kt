package me.spartacus04.colosseum.gui.virtualInventory

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.gui.Gui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


/**
 * An event that represents an interaction with a virtual inventory. This event is fired when a player interacts with a virtual inventory, such as clicking on an item or dragging items in the inventory. It contains information about the interaction, such as the type of click, the item being interacted with, and the changes being made to the virtual inventory. This event can be listened to in order to handle interactions with virtual inventories and update them accordingly.
 *
 * The main purpose of this event is to provide a way to handle interactions with virtual inventories in a consistent way, regardless of the specific implementation of the virtual inventory. By providing information about the interaction and the changes being made to the virtual inventory, this event allows for flexible handling of virtual inventory interactions and can be used to create complex and dynamic GUIs.
 */
class VirtualInventoryInteractEvent {

    /**
     * Represents a change to a specific slot in the virtual inventory as a result of an interaction. This class contains information about the physical slot that was interacted with, the corresponding virtual slot in the virtual inventory, the old item that was in the slot before the interaction, and the new item that will be in the slot after the interaction. It also provides helper properties to determine if the change is an addition, removal, update, or swap of items.
     *
     * @param physicalSlot The physical slot index in the player's inventory that was interacted with.
     * @param virtualSlot The corresponding virtual slot index in the virtual inventory that is affected by the interaction.
     * @param oldItem The item that was in the slot before the interaction. This can be null if the slot was empty.
     * @param newItem The item that will be in the slot after the interaction. This can be null if the slot will be empty after the interaction.
     * @param virtualInventory The virtual inventory that is being interacted with. This is used to determine which virtual inventory the slot change belongs to, as there may be multiple virtual inventories in the GUI.
     */
    data class SlotChange(
        val physicalSlot: Int,
        val virtualSlot: Int,
        val oldItem: ItemStack?,
        val newItem: ItemStack?,
        val virtualInventory: VirtualInventory
    ) {
        /**
         * Helper property to determine if the change is an addition of an item to the slot (i.e., the old item was null and the new item is not null).
         */
        val isAdd: Boolean
            get() = oldItem == null && newItem != null

        /**
         * Helper property to determine if the change is a removal of an item from the slot (i.e., the old item was not null and the new item is null).
         */
        val isRemove: Boolean
            get() = oldItem != null && newItem == null

        /**
         * Helper property to determine if the change is an update of the item in the slot (i.e., both old and new items are not null and are similar, meaning they are the same type of item with the same metadata).
         */
        val isUpdate: Boolean
            get() = oldItem != null && newItem != null && oldItem.isSimilar(newItem)

        /**
         * Helper property to determine if the change is a swap of items in the slot (i.e., both old and new items are not null and are not similar, meaning they are different types of items or have different metadata).
         */
        val isSwap: Boolean
            get() = oldItem != null && newItem != null && !oldItem.isSimilar(newItem)
    }

    /**
     * The underlying Bukkit event that triggered this virtual inventory interaction. This can be either an InventoryClickEvent or an InventoryDragEvent, depending on the type of interaction. This property provides access to the original event for any additional information that may be needed to handle the interaction, such as the player who interacted, the inventory involved, and other details specific to the type of event.
     */
    val mcEvent: InventoryInteractEvent

    /**
     * The GUI that fired the event.
     */
    val gui: Gui

    /**
     * The type of inventory action that was performed in the interaction, such as picking up an item, placing an item, swapping with the cursor, etc. This is determined by the underlying Bukkit event and provides information about what kind of interaction occurred, which can be used to handle the interaction appropriately.
     */
    val inventoryAction: InventoryAction

    /**
     * The type of click that was performed in the interaction, such as left-click, right-click, shift-click, etc. This is determined by the underlying Bukkit event and provides information about how the player interacted with the inventory, which can be used to handle the interaction appropriately.
     */
    val clickType: ClickType

    /**
     * The item that was on the cursor before the interaction occurred. This can be null if the cursor was empty. This property provides information about what item the player was holding with their cursor before the interaction, which can be used to determine how to handle the interaction and update the virtual inventory accordingly.
     */
    val oldCursor: ItemStack?

    /**
     * The item that will be on the cursor after the interaction occurs. This can be null if the cursor will be empty after the interaction. This property provides information about what item the player will be holding with their cursor after the interaction, which can be used to determine how to handle the interaction and update the virtual inventory accordingly.
     */
    val newCursor: ItemStack?

    /**
     * A list of changes to the slots in the virtual inventory that will occur as a result of the interaction. Each SlotChange in the list represents a change to a specific slot in the virtual inventory, including the physical slot that was interacted with, the corresponding virtual slot, the old item in that slot, and the new item that will be in that slot after the interaction. This list provides a comprehensive overview of how the virtual inventory will be affected by the interaction, allowing for flexible handling and updating of the virtual inventory based on the player's actions.
     */
    val slotChanges: List<SlotChange>

    /**
     * Indicates whether the interaction should be cancelled. Setting this to true will prevent the default behavior of the underlying Bukkit event, allowing for custom handling of the interaction without interference from the default mechanics. This can be used to implement custom rules or restrictions on how players can interact with the virtual inventory, such as preventing certain types of interactions or enforcing specific behaviors when interacting with certain items or slots.
     */
    var isCancelled: Boolean
        get() = mcEvent.isCancelled
        set(value) { mcEvent.isCancelled = value }

    constructor(event: VirtualInventoryInteractEvent, virtualInventory: VirtualInventory) {
        this.mcEvent = event.mcEvent
        this.gui = event.gui
        this.inventoryAction = event.inventoryAction
        this.clickType = event.clickType
        this.oldCursor = event.oldCursor
        this.newCursor = event.newCursor

        this.slotChanges = event.slotChanges.filter { it.virtualInventory == virtualInventory }
    }

    constructor(mcEvent: InventoryDragEvent, gui: Gui) {
        this.mcEvent = mcEvent
        this.gui = gui

        oldCursor = mcEvent.oldCursor
        newCursor = mcEvent.cursor
        clickType = ClickType.LEFT
        inventoryAction = InventoryAction.PLACE_SOME

        slotChanges = mcEvent.newItems.mapNotNull {
            val provider = gui.structure[it.key]
            if(provider == null || provider !is VirtualInventory.VirtualInventoryItemProvider) return@mapNotNull null

            SlotChange(
                physicalSlot = it.key,
                virtualSlot = provider.slot,
                oldItem = provider.virtualInventory.getItem(provider.slot),
                newItem = it.value,
                virtualInventory = provider.virtualInventory
            )
        }
    }

    constructor(mcEvent: InventoryClickEvent, gui: Gui, virtualInventory: VirtualInventory) : this(
        VirtualInventoryInteractEvent(mcEvent, gui),
        virtualInventory
    )

    constructor(mcEvent: InventoryClickEvent, gui: Gui) {
        this.mcEvent = mcEvent
        this.gui = gui
        inventoryAction = mcEvent.action
        clickType = mcEvent.click
        oldCursor = mcEvent.cursor

        val validSlots = gui.structure.mapIndexedNotNull { index, provider ->
            if(provider !is VirtualInventory.VirtualInventoryItemProvider) return@mapIndexedNotNull null
            index to provider
        }.toMap()

        // calculate slot changes for the clicked slot if it belongs to the virtualInventory
        when(inventoryAction) {
            InventoryAction.CLONE_STACK -> {
                newCursor = mcEvent.currentItem?.clone()?.apply {
                    amount = maxStackSize
                }
                slotChanges = emptyList()
            }

            InventoryAction.DROP_ALL_SLOT -> {
                newCursor = oldCursor
                slotChanges = if(mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!

                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = provider.virtualInventory.getItem(provider.slot),
                            newItem = null,
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.DROP_ONE_SLOT -> {
                newCursor = oldCursor
                slotChanges = if(mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!
                    val oldItem = provider.virtualInventory.getItem(provider.slot)

                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = oldItem,
                            newItem = oldItem?.clone()?.apply {
                                amount -= 1
                            },
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.PICKUP_ALL -> {
                newCursor = mcEvent.currentItem
                slotChanges = if(mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!
                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = provider.virtualInventory.getItem(provider.slot),
                            newItem = null,
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.PICKUP_HALF -> {
                newCursor = mcEvent.currentItem?.clone()?.apply {
                    amount /= 2
                }

                slotChanges = if (mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!
                    val oldItem = provider.virtualInventory.getItem(provider.slot)

                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = oldItem,
                            newItem = oldItem?.clone()?.apply {
                                amount -= newCursor?.amount ?: 0
                            },
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.PICKUP_SOME -> {
                if(mcEvent.cursor.isSimilar(mcEvent.currentItem)) {
                    val combinedAmount = mcEvent.cursor.amount + (mcEvent.currentItem?.amount ?: 0)
                    val maxStackSize = mcEvent.currentItem?.maxStackSize ?: 64

                    newCursor = mcEvent.currentItem?.clone()?.apply {
                        amount = minOf(combinedAmount, maxStackSize)
                    }

                    slotChanges = if(mcEvent.slot in validSlots) {
                        val provider = validSlots[mcEvent.slot]!!
                        val oldItem = provider.virtualInventory.getItem(provider.slot)

                        listOf(
                            SlotChange(
                                physicalSlot = mcEvent.slot,
                                virtualSlot = provider.slot,
                                oldItem = oldItem,
                                newItem = if(combinedAmount > maxStackSize) {
                                    oldItem?.clone()?.apply {
                                        amount = combinedAmount - newCursor!!.amount
                                    }
                                } else {
                                    null
                                },
                                virtualInventory = provider.virtualInventory
                            )
                        )
                    } else {
                        emptyList()
                    }
                } else {
                    isCancelled = true
                    newCursor = oldCursor
                    slotChanges = listOf()
                }
            }

            InventoryAction.PICKUP_ONE -> {
                // Same as before but only pick up one item
                newCursor = mcEvent.currentItem?.clone()?.apply {
                    amount += 1
                }

                slotChanges = if(mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!
                    val oldItem = provider.virtualInventory.getItem(provider.slot)
                    val newAmount = (oldItem?.amount ?: 1) - 1

                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = oldItem,
                            newItem = if(newAmount > 0) {
                                oldItem?.clone()?.apply {
                                    amount = newAmount
                                }
                            } else {
                                null
                            },
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.DROP_ALL_CURSOR -> {
                newCursor = null
                slotChanges = emptyList()
            }

            InventoryAction.DROP_ONE_CURSOR -> {
                newCursor = if(oldCursor.amount != 1) {
                    oldCursor.clone().apply {
                        amount -= 1
                    }
                } else {
                    null
                }
                slotChanges = emptyList()
            }

            InventoryAction.SWAP_WITH_CURSOR -> {
                newCursor = mcEvent.currentItem
                slotChanges = if(mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!
                    val oldItem = provider.virtualInventory.getItem(provider.slot)

                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = oldItem,
                            newItem = oldCursor,
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.PLACE_ALL -> {
                newCursor = null
                slotChanges = if(mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!
                    val oldItem = provider.virtualInventory.getItem(provider.slot)

                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = oldItem,
                            newItem = if(oldItem == null) {
                                oldCursor
                            } else if(oldItem.isSimilar(oldCursor)) {
                                oldItem.clone().apply {
                                    amount += oldCursor.amount
                                }
                            } else {
                                oldItem
                            },
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.PLACE_SOME -> {
                if(mcEvent.cursor.isSimilar(mcEvent.currentItem)) {
                    val combinedAmount = mcEvent.cursor.amount + (mcEvent.currentItem?.amount ?: 0)
                    val maxStackSize = mcEvent.currentItem?.maxStackSize ?: 64

                    newCursor = mcEvent.currentItem?.clone()?.apply {
                        amount = combinedAmount % maxStackSize
                    }

                    slotChanges = if(mcEvent.slot in validSlots) {
                        val provider = validSlots[mcEvent.slot]!!
                        val oldItem = provider.virtualInventory.getItem(provider.slot)

                        listOf(
                            SlotChange(
                                physicalSlot = mcEvent.slot,
                                virtualSlot = provider.slot,
                                oldItem = oldItem,
                                newItem = if(combinedAmount > maxStackSize) {
                                    oldItem?.clone()?.apply {
                                        amount += oldCursor.amount - newCursor!!.amount
                                    }
                                } else {
                                    null
                                },
                                virtualInventory = provider.virtualInventory
                            )
                        )
                    } else {
                        emptyList()
                    }
                } else {
                    isCancelled = true
                    newCursor = oldCursor
                    slotChanges = listOf()
                }
            }

            InventoryAction.PLACE_ONE -> {
                newCursor = if(oldCursor.amount != 1) {
                    oldCursor.clone().apply {
                        amount -= 1
                    }
                } else {
                    null
                }

                slotChanges = if(mcEvent.slot in validSlots) {
                    val provider = validSlots[mcEvent.slot]!!
                    val oldItem = provider.virtualInventory.getItem(provider.slot)

                    listOf(
                        SlotChange(
                            physicalSlot = mcEvent.slot,
                            virtualSlot = provider.slot,
                            oldItem = oldItem,
                            newItem = if(oldItem == null) {
                                oldCursor.clone().apply { amount = 1 }
                            } else if(oldItem.isSimilar(oldCursor)) {
                                oldItem.clone().apply {
                                    amount += 1
                                }
                            } else {
                                oldItem
                            },
                            virtualInventory = provider.virtualInventory
                        )
                    )
                } else {
                    emptyList()
                }
            }

            InventoryAction.HOTBAR_SWAP -> {
                newCursor = oldCursor

                if(mcEvent.hotbarButton == -1) {
                    slotChanges = emptyList()
                } else {
                    val hotbarItem = mcEvent.whoClicked.inventory.getItem(mcEvent.hotbarButton)
                    slotChanges = if(mcEvent.slot in validSlots) {
                        val provider = validSlots[mcEvent.slot]!!
                        val oldItem = provider.virtualInventory.getItem(provider.slot)

                        listOf(SlotChange(
                            physicalSlot = mcEvent.hotbarButton,
                            virtualSlot = provider.slot,
                            oldItem = oldItem,
                            newItem = hotbarItem,
                            virtualInventory = provider.virtualInventory
                        ))
                    } else {
                        emptyList()
                    }
                }
            }

            InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                if(mcEvent.rawSlot in 0 until gui.inventory.size) {
                    newCursor = oldCursor
                    slotChanges = if(mcEvent.rawSlot in validSlots) {
                        val provider = validSlots[mcEvent.rawSlot]!!
                        val oldItem = provider.virtualInventory.getItem(provider.slot)

                        listOf(
                            SlotChange(
                                physicalSlot = mcEvent.rawSlot,
                                virtualSlot = provider.slot,
                                oldItem = oldItem,
                                newItem = null,
                                virtualInventory = provider.virtualInventory
                            )
                        )
                    } else {
                        emptyList()
                    }
                } else {
                    val slot = findTargetSlot(gui.inventory, mcEvent.currentItem!!)

                    if(slot == -1 || slot !in validSlots) {
                        newCursor = oldCursor
                        slotChanges = emptyList()
                    } else {
                        val provider = validSlots[slot]!!
                        val oldItem = provider.virtualInventory.getItem(provider.slot)

                        newCursor = null
                        slotChanges = listOf(
                            SlotChange(
                                physicalSlot = slot,
                                virtualSlot = provider.slot,
                                oldItem = oldItem,
                                newItem = if (oldItem?.isSimilar(mcEvent.currentItem) == true) {
                                    oldItem.clone().apply {
                                        amount += mcEvent.currentItem!!.amount % oldItem.maxStackSize
                                    }
                                } else {
                                    mcEvent.currentItem
                                },
                                virtualInventory = provider.virtualInventory
                            )
                        )
                    }
                }
            }

            else -> {
                isCancelled = true
                newCursor = oldCursor
                slotChanges = emptyList()
            }
        }
    }

    /**
     * The player who interacted with the virtual inventory.
     */
    val player: Player
        get() = gui.player

    /**
     * Helper property to determine if the click was a shift-click, which is a common modifier in inventory interactions that can indicate different behavior (e.g., moving items between inventories). This is determined by checking the ClickType of the interaction and can be used to handle shift-click interactions differently from regular clicks.
     */
    val isShiftClick: Boolean
        get() = clickType.isShiftClick

    /**
     * Helper property to determine if the click was a right-click, which can indicate different behavior in inventory interactions (e.g., placing items one at a time). This is determined by checking the ClickType of the interaction and can be used to handle right-click interactions differently from left-clicks.
     */
    val isRightClick: Boolean
        get() = clickType.isRightClick

    /**
     * Helper property to determine if the click was a left-click, which is the default type of click in inventory interactions. This is determined by checking the ClickType of the interaction and can be used to handle left-click interactions differently from right-clicks or shift-clicks.
     */
    val isLeftClick: Boolean
        get() = clickType.isLeftClick

    /**
     * The instance of the ColosseumPlugin that is associated with this event. This is accessed through the GUI that fired the event, as the GUI is created and managed by the ColosseumPlugin. This property provides access to the plugin instance for any additional functionality or information that may be needed to handle the virtual inventory interaction, such as accessing configuration, scheduling tasks, or interacting with other parts of the plugin.
     */
    val plugin: ColosseumPlugin
        get() = gui.plugin

    private fun findTargetSlot(inv: Inventory, item: ItemStack): Int {
        for (i in 0..<inv.size) {
            val slot = inv.getItem(i)
            if (slot != null && slot.isSimilar(item) && slot.amount < slot.maxStackSize) {
                return i
            }
        }

        for (i in 0..<inv.size) {
            if (inv.getItem(i) == null || inv.getItem(i)!!.type === Material.AIR) {
                return i
            }
        }

        return -1
    }
}