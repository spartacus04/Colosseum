package me.spartacus04.colosseum.gui

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.listeners.ColosseumListener
import me.spartacus04.colosseum.gui.items.AbstractItemProvider
import me.spartacus04.colosseum.gui.items.ScrollItemProvider
import me.spartacus04.colosseum.gui.items.SimpleItemProvider
import me.spartacus04.colosseum.gui.virtualInventory.VirtualInventory
import me.spartacus04.colosseum.gui.virtualInventory.VirtualInventoryInteractEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class Gui private constructor(
    title: String? = null,
    size: Int = 3,
    val player: Player,
    val structure: Array<AbstractItemProvider?>,
    val virtualInventories: HashMap<Int, VirtualInventoryData>,
    val plugin: ColosseumPlugin,
    allowClose: Boolean = false,
) {
    fun handleInventoryScroll(inventoryId: Int, scrollAmount: Int) {
        if(!canScroll(inventoryId, scrollAmount)) return

        var scroll = scrollAmount
        val virtualInvData = virtualInventories[inventoryId] ?: return
        val window = virtualInvScrollingWindow(inventoryId) ?: return

        if (window.first + scrollAmount < 0) {
            scroll = -window.first
        }

        virtualInvData.providers.forEach {
            it.slot += scroll
        }

        structure
            .filter { it is ScrollItemProvider && it.inventoryId == inventoryId }
            .forEach { it!!.refresh() }
    }

    fun canScroll(inventoryId: Int, scrollAmount: Int): Boolean {
        val virtualInvData = virtualInventories[inventoryId] ?: return false
        val scrollWindow = virtualInvScrollingWindow(inventoryId)!!

        return !(scrollAmount > 0 && scrollWindow.second >= virtualInvData.inventory.size - 1) &&
                !(scrollAmount < 0 && scrollWindow.first <= 0)
    }

    fun virtualInvScrollingWindow(inventoryId: Int): Pair<Int, Int>? {
        val virtualInvData = virtualInventories[inventoryId] ?: return null

        return Pair(
            virtualInvData.providers[0].slot,
            virtualInvData.providers[virtualInvData.providers.size - 1].slot,
        )
    }

    fun refreshVirtualInventory(inventoryId: Int) {
        val virtualInvData = virtualInventories[inventoryId] ?: return
        virtualInvData.providers.forEach { it.refresh() }
    }

    fun refreshVirtualInventorySlot(inventoryId: Int, slot: Int) {
        val virtualInvData = virtualInventories[inventoryId] ?: return
        val provider = virtualInvData.providers.firstOrNull { it.slot == slot } ?: return
        provider.refresh()
    }

    val inventory = if(title != null) {
        Bukkit.createInventory(null, size * 9, title)
    } else {
        Bukkit.createInventory(null, size * 9)
    }

    internal val guiListener = GuiListener(this, plugin, allowClose)

    init {
        structure.forEachIndexed { index, provider ->
            provider?.setGuiData(this, index)
        }

        virtualInventories.keys.forEach {
            virtualInventories[it]!!.inventory.registerGui(this, it)
        }

        refreshAll()
        open()
    }

    fun refreshAll() {
        structure.forEachIndexed { index, provider ->
            val item = provider?.getItem()
            if(item != null) {
                inventory.setItem(index, item)
            }
        }
    }

    fun refreshIndex(index: Int) {
        if(index < structure.size) {
            val item = structure[index]?.getItem()
            inventory.setItem(index, item)
        }
    }

    fun open() {
        guiListener.register()
        player.openInventory(inventory)
    }

    fun close() {
        player.closeInventory()
        guiListener.unregister()
        structure.forEach { it?.destroy() }
        virtualInventories.values.forEach { it.inventory.unRegisterGui(this) }
    }

    open fun onDrag(event: InventoryDragEvent) {
        if(
            event.rawSlots
                .filter { it < inventory.size }
                .any { structure[it] !is VirtualInventory.VirtualInventoryItemProvider }
        ) {
            event.isCancelled = true
            return
        }

        val event = VirtualInventoryInteractEvent(event, this)
        val virtualInvs = event.slotChanges.map { it.virtualInventory }.distinct()

        if(virtualInvs.size == 1) {
            virtualInvs[0].handleClick(event)
        }
    }

    open fun onClick(clickEvent: InventoryClickEvent) {
        val slot = clickEvent.rawSlot

        if(slot < 0) return

        if(slot < structure.size && structure[slot] != null) {
            if(structure[slot] is VirtualInventory.VirtualInventoryItemProvider) {
                val virtualInv = (structure[slot] as VirtualInventory.VirtualInventoryItemProvider).virtualInventory

                val event = VirtualInventoryInteractEvent(
                    clickEvent,
                    this,
                    virtualInv,
                )

                virtualInv.handleClick(event)
            } else {
                clickEvent.isCancelled = true
                structure[slot]!!.onClick(clickEvent)
                refreshIndex(slot)
            }
        }
    }

    data class VirtualInventoryData(
        val inventory: VirtualInventory,
        val providers: List<VirtualInventory.VirtualInventoryItemProvider>
    )

    class Builder(val plugin: ColosseumPlugin) {
        enum class DirectionMarker {
            HORIZONTAL,
            VERTICAL
        }

        internal data class InventoryBindingData(
            val id: Int,
            val virtualInventory: VirtualInventory,
            val directionMarker: DirectionMarker,
            var counter: Int = 0,
            val providers: ArrayList<VirtualInventory.VirtualInventoryItemProvider> = arrayListOf(),
        )

        private val structure: ArrayList<String> = arrayListOf()
        private val itemBindings: HashMap<Char, AbstractItemProvider> = hashMapOf()
        private var inventoryBindingsCount = 0
        private val inventoryBindings: HashMap<Char, InventoryBindingData> = hashMapOf()
        private var player: Player? = null
        private var allowClose: Boolean = true
        private var title: String? = null

        fun setBinding(char: Char, provider: AbstractItemProvider): Builder {
            if(char == ' ') {
                throw IllegalArgumentException("Bindings cannot use character ' '")
            }

            if(itemBindings.containsKey(char)) {
                throw IllegalArgumentException("Item $char already registered!")
            }

            itemBindings[char] = provider
            return this
        }

        fun setBinding(char: Char, itemStack: ItemStack): Builder {
            return setBinding(char, SimpleItemProvider(itemStack))
        }

        fun setBinding(char: Char, virtualInventory: VirtualInventory, marker: DirectionMarker = DirectionMarker.HORIZONTAL, id: Int = -1): Builder {
            if(char == ' ') {
                throw IllegalArgumentException("Bindings cannot use character ' '")
            }

            if(inventoryBindings.contains(char)) {
                throw IllegalArgumentException("VirtualInventory $char already registered")
            }

            inventoryBindings[char] = InventoryBindingData(
                if(id < 0) {
                    inventoryBindingsCount
                } else {
                    id
                },
                virtualInventory,
                marker
            )

            inventoryBindingsCount++

            return this
        }

        fun setStructure(vararg strings: String): Builder {
            structure.clear()
            structure.addAll(strings)
            return this
        }

        fun setPlayer(player: Player): Builder {
            this.player = player
            return this
        }

        fun allowClose(allow: Boolean): Builder {
            allowClose = allow
            return this
        }

        fun setTitle(string: String): Builder {
            title = string
            return this
        }

        fun buildAndOpen() : Gui {
            if(player == null) {
                throw IllegalArgumentException("A player must be specified")
            }

            // Check inventory height
            if(structure.size > 6) {
                throw IllegalArgumentException("Guis rows must be <= 6")
            } else if(structure.isEmpty()) {
                structure.add("")
            }

            // mutual exclusivity between itemBinding keys and inventoryBinding keys
            inventoryBindings.keys.forEach {
                if(itemBindings.keys.contains(it)) {
                    throw IllegalArgumentException("Same binding cannot be used by an ItemProvider and a VirtualInventory")
                }
            }

            // Prepare structure in an n * 9 grid
            structure.forEachIndexed { index, string ->
                structure[index] = string.replace(" ", "")

                if(structure[index].length > 9) {
                    throw IllegalArgumentException("Guis size exceeds row size at row ${index + 1}, must be <= 9")
                } else if(structure[index].length < 9) {
                    structure[index] = string.padEnd(0, ' ')
                }
            }

            // Build final structure
            val itemStructure = Array(structure.size) {
                arrayOfNulls<AbstractItemProvider>(9)
            }

            for(y in 0 ..< structure.size) {
                for(x in 0 .. 8) {
                    val char = structure[y][x]

                    if(itemBindings.containsKey(char)) {
                        itemStructure[y][x] = itemBindings[char]!!
                    } else if (inventoryBindings.contains(char) && inventoryBindings[char]!!.directionMarker == DirectionMarker.HORIZONTAL) {
                        val invData = inventoryBindings[char]!!

                        val provider = VirtualInventory.VirtualInventoryItemProvider(
                            invData.virtualInventory,
                            invData.counter
                        )

                        invData.counter++
                        invData.providers.add(provider)

                        itemStructure[y][x] = provider
                    }
                }
            }

            for(x in 0 .. 8) {
                for(y in 0 ..< structure.size) {
                    val char = structure[y][x]

                    if(inventoryBindings.contains(char) && inventoryBindings[char]!!.directionMarker == DirectionMarker.VERTICAL) {
                        val invData = inventoryBindings[char]!!

                        val provider = VirtualInventory.VirtualInventoryItemProvider(
                            invData.virtualInventory,
                            invData.counter
                        )

                        invData.counter++
                        invData.providers.add(provider)

                        itemStructure[y][x] = provider
                    }
                }
            }

            return Gui(
                title,
                structure.size,
                player!!,
                itemStructure.flatten().toTypedArray(),
                HashMap(
                    inventoryBindings.values.associate {
                        it.id to VirtualInventoryData(it.virtualInventory, it.providers)
                    }
                ),
                plugin
            )
        }
    }

    internal class GuiListener(val gui: Gui, val plugin: ColosseumPlugin, val allowClose: Boolean) : ColosseumListener(plugin) {
        @EventHandler
        fun onClick(event: InventoryClickEvent) {
            if (event.whoClicked != gui.player) return
            if (event.rawSlot >= gui.inventory.size) return
            if (event.view.topInventory != gui.inventory) return

            gui.onClick(event)
        }

        @EventHandler
        fun onDrag(event: InventoryDragEvent) {
            if (event.whoClicked != gui.player) return
            if (event.view.topInventory != gui.inventory) return

            gui.onDrag(event)
        }

        @EventHandler
        fun onClose(event: InventoryCloseEvent) {
            if(allowClose && event.player == gui.player && event.view.topInventory == gui.inventory) {
                gui.close()
            }
        }
    }

    companion object {
        fun builder(plugin: ColosseumPlugin): Builder {
            return Builder(plugin)
        }

        fun buildAndOpen(plugin: ColosseumPlugin, init: Builder.() -> Unit): Gui {
            return builder(plugin).apply(init).buildAndOpen()
        }
    }
}