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

/**
 * A class representing a GUI in the Colosseum plugin. This class is responsible for managing the inventory, handling clicks and drags, and refreshing the items in the GUI when necessary.
 *
 * The GUI is built using a structure of AbstractItemProviders, which are responsible for providing the items to display in each slot of the inventory and handling clicks on those items. The GUI also supports virtual inventories.
 *
 * To create a GUI, use the Builder class to define the structure of the GUI, set up item bindings and virtual inventory bindings, and then call buildAndOpen() to create and open the GUI for a player.
 *
 * @param title The title of the GUI. This will be displayed at the top of the inventory when the GUI is open. If null, the default inventory title will be used.
 * @param size The number of rows in the GUI. Each row contains 9 slots, so the total number of slots in the GUI will be size * 9. The maximum size is 6 rows
 * @param player The player for whom the GUI will be opened. This is required to open the inventory and handle click events.
 * @param structure An array of AbstractItemProviders that defines the items to display in each slot
 * @param virtualInventories A map of virtual inventory IDs to their corresponding VirtualInventoryData, which contains the VirtualInventory instance and the list of providers associated with that virtual inventory. This is used to manage virtual inventories within the GUI and handle scrolling and refreshing of those inventories.
 * @param plugin The instance of the ColosseumPlugin, used for registering listeners and scheduling tasks related to the GUI.
 * @param allowClose A boolean flag that determines whether the GUI can be closed by the player.
 */
class Gui private constructor(
    title: String? = null,
    size: Int = 3,
    val player: Player,
    val structure: Array<AbstractItemProvider?>,
    val virtualInventories: HashMap<Int, VirtualInventoryData>,
    val plugin: ColosseumPlugin,
    allowClose: Boolean = false,
) {
    /**
     * Handles scrolling for a virtual inventory. This method is called when a ScrollItemProvider is clicked, and it updates the slot of each provider in the virtual inventory by the specified scroll amount. It also refreshes the items in the GUI that are associated with the virtual inventory.
     *
     * @param inventoryId The ID of the virtual inventory to scroll. This should correspond to the ID specified when the virtual inventory was bound in the Builder.
     * @param scrollAmount The amount to scroll the virtual inventory. This is typically 1 for scrolling down and -1 for scrolling up, but it can be any integer value depending on how you want to implement scrolling behavior.
     */
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

    /**
     * Checks if a virtual inventory can be scrolled by the specified amount. This method is used to prevent scrolling beyond the bounds of the virtual inventory, which could cause errors or unexpected behavior.
     *
     * @param inventoryId The ID of the virtual inventory to check. This should correspond to the ID specified when the virtual inventory was bound in the Builder.
     * @param scrollAmount The amount to scroll the virtual inventory.
     */
    fun canScroll(inventoryId: Int, scrollAmount: Int): Boolean {
        val virtualInvData = virtualInventories[inventoryId] ?: return false
        val scrollWindow = virtualInvScrollingWindow(inventoryId)!!

        return !(scrollAmount > 0 && scrollWindow.second >= virtualInvData.inventory.size - 1) &&
                !(scrollAmount < 0 && scrollWindow.first <= 0)
    }

    /**
     * Calculates the current scrolling window for a virtual inventory. This method returns a pair of integers representing the first and last slot indices of the virtual inventory that are currently being displayed in the GUI. This is used to determine which items from the virtual inventory should be shown based on the current scroll position.
     *
     * @param inventoryId The ID of the virtual inventory to calculate the scrolling window for. This should correspond to the ID specified when the virtual inventory was bound in the Builder.
     */
    fun virtualInvScrollingWindow(inventoryId: Int): Pair<Int, Int>? {
        val virtualInvData = virtualInventories[inventoryId] ?: return null

        return Pair(
            virtualInvData.providers[0].slot,
            virtualInvData.providers[virtualInvData.providers.size - 1].slot,
        )
    }

    /**
     * Refreshes all providers associated with a virtual inventory. This method is used to update the items displayed in the GUI for a virtual inventory, typically after scrolling or when the contents of the virtual inventory have changed. It calls the refresh() method on each provider associated with the specified virtual inventory, which will update the items in the GUI accordingly.
     *
     * @param inventoryId The ID of the virtual inventory to refresh. This should correspond to the ID specified when the virtual inventory was bound in the Builder.
     */
    fun refreshVirtualInventory(inventoryId: Int) {
        val virtualInvData = virtualInventories[inventoryId] ?: return
        virtualInvData.providers.forEach { it.refresh() }
    }

    /**
     * Refreshes a specific slot for a virtual inventory. This method is used to update a single slot in the GUI that is associated with a virtual inventory, typically after scrolling or when the contents of the virtual inventory have changed. It calls the refresh() method on the provider associated with the specified slot in the virtual inventory, which will update the item in the GUI for that slot accordingly.
     *
     * @param inventoryId The ID of the virtual inventory to refresh. This should correspond to the ID specified when the virtual inventory was bound in the Builder.
     * @param slot The slot index within the virtual inventory to refresh.
     */
    fun refreshVirtualInventorySlot(inventoryId: Int, slot: Int) {
        val virtualInvData = virtualInventories[inventoryId] ?: return
        val provider = virtualInvData.providers.firstOrNull { it.slot == slot } ?: return
        provider.refresh()
    }

    /**
     * The inventory object representing the GUI. This is the actual Bukkit inventory that is opened for the player and contains the items displayed in the GUI. The inventory is created based on the specified size and title, and it is populated with items from the structure of AbstractItemProviders when the GUI is initialized. The inventory is also updated whenever providers are refreshed or when virtual inventories are scrolled.
     */
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

    /**
     * Refreshes all items in the GUI by calling the getItem() method on each provider in the structure and updating the corresponding slot in the inventory.
     */
    fun refreshAll() {
        structure.forEachIndexed { index, provider ->
            val item = provider?.getItem()
            if(item != null) {
                inventory.setItem(index, item)
            }
        }
    }

    /**
     * Refreshes a specific index in the GUI by calling the getItem() method on the provider at that index in the structure and updating the corresponding slot in the inventory.
     */
    fun refreshIndex(index: Int) {
        if(index < structure.size) {
            val item = structure[index]?.getItem()
            inventory.setItem(index, item)
        }
    }

    /**
     * Opens the GUI for the player by registering the GUI listener and opening the inventory. This method is called when the GUI is initialized, and it ensures that the player can interact with the GUI and that click and drag events are properly handled by the listener.
     */
    fun open() {
        guiListener.register()
        player.openInventory(inventory)
    }

    /**
     * Closes the GUI for the player by closing the inventory, unregistering the GUI listener, destroying any providers in the structure, and unregistering the GUI from any virtual inventories. This method is called when the player closes the inventory or when allowClose is true and the player attempts to close the GUI. It ensures that all resources associated with the GUI are properly cleaned up to prevent memory leaks or unintended behavior after the GUI is closed.
     */
    fun close() {
        player.closeInventory()
        guiListener.unregister()
        structure.forEach { it?.destroy() }
        virtualInventories.values.forEach { it.inventory.unRegisterGui(this) }
    }

    /**
     * Handles drag events in the inventory. Drag events can only involve a single virtual inventory, and if the drag involves any slots that are not associated with a virtual inventory, the event is cancelled.
     *
     * @param event The InventoryDragEvent
     */
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

    /**
     * Handles click events in the inventory. When a slot is clicked, this method checks if the slot corresponds to a provider in the structure and calls the onClick() method of that provider. If the provider is a VirtualInventoryItemProvider, it creates a VirtualInventoryInteractEvent and passes it to the virtual inventory's handleClick() method. This allows for proper handling of clicks on both regular items and virtual inventory items within the GUI.
     *
     * @param clickEvent the InventoryClickEvent
     */
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

    /**
     * A data class representing the data associated with a virtual inventory within the GUI.
     *
     * @param inventory The VirtualInventory instance associated with this data. This is the virtual inventory that is being managed within the GUI.
     * @param providers The list of VirtualInventoryItemProviders that are associated with this virtual inventory.
     */
    data class VirtualInventoryData(
        val inventory: VirtualInventory,
        val providers: List<VirtualInventory.VirtualInventoryItemProvider>
    )

    /**
     * A builder class for constructing a Gui instance. This class provides a fluent API for defining the structure of the GUI, setting up item bindings and virtual inventory bindings, and configuring other properties of the GUI before building and opening it for a player.
     *
     * @param plugin The instance of the ColosseumPlugin
     */
    class Builder(val plugin: ColosseumPlugin) {
        /**
         * An enum representing the direction in which a virtual inventory should be laid out in the GUI.
         */
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

        /**
         * Binds a character to an AbstractItemProvider. This allows you to use the specified character in the structure of the GUI to indicate where the item provided by the AbstractItemProvider should be displayed. When the GUI is built, any occurrence of the specified character in the structure will be replaced with the item from the AbstractItemProvider.
         *
         * Note that the character ' ' (space) cannot be used for bindings. Additionally, each character can only be bound to one AbstractItemProvider, so if you try to bind a character that has already been used, an IllegalArgumentException will be thrown.
         *
         * @param char The character to bind to the AbstractItemProvider.
         * @param provider The AbstractItemProvider that will provide the item to display in the GUI for any occurrence of the specified character in the structure.
         */
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

        /**
         * A convenience method for binding a character to a SimpleItemProvider that provides a specific ItemStack. This allows you to easily bind a character in the structure to a static item without needing to create a separate AbstractItemProvider class for it. The method creates a new SimpleItemProvider with the provided ItemStack and binds it to the specified character.
         *
         * Note that the character ' ' (space) cannot be used for bindings. Additionally, each character can only be bound to one AbstractItemProvider, so if you try to bind a character that has already been used, an IllegalArgumentException will be thrown.
         *
         * @param char The character to bind to the SimpleItemProvider.
         * @param itemStack The ItemStack that will be provided by the SimpleItemProvider.
         */
        fun setBinding(char: Char, itemStack: ItemStack): Builder {
            return setBinding(char, SimpleItemProvider(itemStack))
        }

        /**
         * Binds a character to a VirtualInventory. This allows you to use the specified character in the structure of the GUI to indicate where the items from the VirtualInventory should be displayed. When the GUI is built, any occurrence of the specified character in the structure will be associated with the VirtualInventory, and the items from that inventory will be displayed in those slots according to the specified direction (horizontal or vertical).
         * Note that the character ' ' (space) cannot be used for bindings. Additionally, each character can only be bound to one VirtualInventory, so if you try to bind a character that has already been used, an IllegalArgumentException will be thrown.
         *
         * @param char The character to bind to the VirtualInventory.
         * @param virtualInventory The VirtualInventory that will be associated with any occurrence of the specified character in the structure. The items from this inventory will be displayed in the GUI in the slots corresponding to that character.
         * @param marker The direction in which the items from the VirtualInventory should be laid out in the GUI. If HORIZONTAL, the items will be displayed in a row from left to right. If VERTICAL, the items will be displayed in a column from top to bottom. The default is HORIZONTAL.
         * @param id An optional ID for the virtual inventory. If not specified, a unique ID will be automatically assigned incrementally (starting from 0) based on the number of virtual inventory bindings.
         */
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

        /**
         * Sets the structure of the GUI using an array of strings. Each string represents a row in the GUI, and each character in the string corresponds to a slot in that row. The characters in the structure should correspond to the characters used in the item bindings and virtual inventory bindings to indicate where those items should be displayed in the GUI. The character ' ' (space) is ignored for clarity.
         *
         * @param strings An array of strings representing the structure of the GUI. Each string corresponds to a row in the GUI, and each character corresponds to a slot in that row. The characters should match those used in the item bindings and virtual inventory bindings to indicate where items should be displayed.
         */
        fun setStructure(vararg strings: String): Builder {
            structure.clear()
            structure.addAll(strings)
            return this
        }

        /**
         * Sets the player for whom the GUI will be opened. This is required.
         *
         * @param player The player
         */
        fun setPlayer(player: Player): Builder {
            this.player = player
            return this
        }

        /**
         * Sets whether the GUI can be closed by the player.
         *
         * @param allow If true, the player can close the GUI. If false, attempts to close the GUI will be ignored and the GUI will remain open until closed programmatically. The default is true.
         */
        fun allowClose(allow: Boolean): Builder {
            allowClose = allow
            return this
        }

        /**
         * Sets the title of the GUI. This is the text that will be displayed at the top of the inventory when the GUI is open. If not set, the default inventory title will be used.
         */
        fun setTitle(string: String): Builder {
            title = string
            return this
        }

        /**
         * Builds the Gui instance based on the defined structure, item bindings, virtual inventory bindings, and other properties set in the Builder. This method performs validation checks on the structure and bindings to ensure that they are properly defined and do not contain conflicts. If any validation checks fail, an IllegalArgumentException will be thrown with a message indicating the issue. If all checks pass, a new Gui instance will be created and opened for the specified player.
         */
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