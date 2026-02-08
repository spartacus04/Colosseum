package me.spartacus04.colosseum.gui.items

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

class AsyncItemProvider(val placeholder: ItemStack, val providerFuture: CompletableFuture<AbstractItemProvider>) : AbstractItemProvider() {
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