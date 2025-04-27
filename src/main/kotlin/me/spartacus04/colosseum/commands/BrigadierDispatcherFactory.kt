package me.spartacus04.colosseum.commands

import com.mojang.brigadier.CommandDispatcher
import me.spartacus04.colosseum.commands.reflection.R1Provider
import me.spartacus04.colosseum.commands.reflection.R2Provider
import me.spartacus04.colosseum.utils.version.MinecraftServerVersion
import org.bukkit.Bukkit

object BrigadierDispatcherFactory {
    fun getDispatcher(): CommandDispatcher<ColosseumSourceStack> {
        for(provider in arrayListOf(
            R1Provider(),
            R2Provider()
        )) {
            if(MinecraftServerVersion.current.isRevisionAnnotationCompatible(provider.javaClass)) {
                return provider.getBrigadier(Bukkit.getServer())
            }
        }

        throw IllegalStateException("No compatible Brigadier provider found for version ${MinecraftServerVersion.current}")
    }
}