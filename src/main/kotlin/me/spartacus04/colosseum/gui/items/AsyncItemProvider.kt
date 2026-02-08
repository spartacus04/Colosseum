package me.spartacus04.colosseum.gui.items

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

/**
 * An ItemProvider that can be used when the item is not immediately available, such as when it needs to be loaded from a database or generated asynchronously.
 * It will display a placeholder item until the actual item is available.
 *
 * @param placeholder The item to display while the actual item is being loaded.
 * @param providerFuture A future that will complete with the actual item provider once it is available
 */
class AsyncItemProvider(val placeholder: ItemStack, providerFuture: CompletableFuture<AbstractItemProvider>) : AbstractItemProvider() {
    /**
     * The resolved item provider once the future is complete. Will be null until the future completes.
     */
    var resolvedProvider: AbstractItemProvider? = null

    init {
        providerFuture.whenComplete { provider, throwable ->
            if(throwable != null) {
                println(throwable)
                return@whenComplete
            }

            resolvedProvider = provider
            refresh()
        }
    }

    override fun getItem(): ItemStack? {
        if(resolvedProvider != null) {
            return resolvedProvider?.getItem()
        }

        return placeholder
    }

    override fun onClick(
        clickEvent: InventoryClickEvent,
    ) = resolvedProvider?.onClick(clickEvent) ?: Unit
}