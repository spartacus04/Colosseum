package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender

/**
 * ArgumentMaterial is a parsable argument that represents a Minecraft material.
 * It suggests valid material names for auto-completion.
 */
class ArgumentMaterial : Argument<Material>() {
    val materialNames = Material.entries.map { listOf(it.name.lowercase(), "minecraft:${it.name.lowercase()}") }.flatten()

    override fun parse(input: String, sender: CommandSender): Material {
        val material = Material.matchMaterial(input.removePrefix("minecraft:").uppercase())

        return material ?: throw MalformedArgumentException(input, "Valid material name")
    }

    override fun suggest(
        input: String,
        sender: CommandSender
    ): List<String> {
        return materialNames.filter { it.startsWith(input.lowercase()) }
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}material${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}material${ChatColor.RESET}>"
}