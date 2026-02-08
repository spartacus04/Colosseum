package me.spartacus04.colosseum.gui.virtualInventory

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.gui.Gui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


class VirtualInventoryInteractEvent {
    data class SlotChange(
        val physicalSlot: Int,
        val virtualSlot: Int,
        val oldItem: ItemStack?,
        val newItem: ItemStack?,
        val virtualInventory: VirtualInventory
    ) {
        val isAdd: Boolean
            get() = oldItem == null && newItem != null

        val isRemove: Boolean
            get() = oldItem != null && newItem == null

        val isUpdate: Boolean
            get() = oldItem != null && newItem != null && oldItem.isSimilar(newItem)

        val isSwap: Boolean
            get() = oldItem != null && newItem != null && !oldItem.isSimilar(newItem)
    }

    val mcEvent: InventoryInteractEvent
    val gui: Gui

    val inventoryAction: InventoryAction
    val clickType: ClickType
    val oldCursor: ItemStack?
    val newCursor: ItemStack?
    val slotChanges: List<SlotChange>

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

    val player: Player
        get() = gui.player

    val isShiftClick: Boolean
        get() = clickType.isShiftClick

    val isRightClick: Boolean
        get() = clickType.isRightClick

    val isLeftClick: Boolean
        get() = clickType.isLeftClick

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