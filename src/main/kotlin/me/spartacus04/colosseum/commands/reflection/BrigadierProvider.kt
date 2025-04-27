package me.spartacus04.colosseum.commands.reflection

import com.mojang.brigadier.CommandDispatcher
import me.spartacus04.colosseum.commands.ColosseumSourceStack
import org.bukkit.Server

interface BrigadierProvider {
    fun getBrigadier(server: Server) : CommandDispatcher<ColosseumSourceStack>
}