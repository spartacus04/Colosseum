package me.spartacus04.colosseum.commands.reflection

import com.mojang.brigadier.CommandDispatcher
import me.spartacus04.colosseum.commands.ColosseumSourceStack
import me.spartacus04.colosseum.utils.version.MinecraftRevisions
import me.spartacus04.colosseum.utils.version.RevisionCompatibilityRange
import org.bukkit.Server

@RevisionCompatibilityRange(MinecraftRevisions.R1, MinecraftRevisions.R7)
class R1Provider : BrigadierProvider {
    override fun getBrigadier(server: Server): CommandDispatcher<ColosseumSourceStack> {

        val getServer = server.javaClass.getMethod("getServer")
        getServer.isAccessible = true
        val craftServer = getServer.invoke(server)

        val getVanillaCommandDispatcher = craftServer.javaClass.getField("vanillaCommandDispatcher")
        getVanillaCommandDispatcher.isAccessible = true
        val vanillaCommandDispatcher = getVanillaCommandDispatcher.get(craftServer)

        val getCommandDispatcher = vanillaCommandDispatcher.javaClass.getMethod("a")
        getCommandDispatcher.isAccessible = true
        val commandDispatcher = getCommandDispatcher.invoke(vanillaCommandDispatcher)

        if (commandDispatcher is CommandDispatcher<*>) {
            return commandDispatcher as CommandDispatcher<ColosseumSourceStack>
        }

        throw IllegalStateException("Vanilla command dispatcher is not supported for this version of Minecraft: ${server.javaClass.name} (${server.version})")
    }
}