package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * ArgumentBoolean is a parsable argument that represents a boolean value ("true" or "false").
 */
class ArgumentBoolean : Argument<Boolean>() {
    override fun parse(input: String, sender: org.bukkit.command.CommandSender): Boolean {
        val str = input.lowercase()

        if (str != "true" && str != "false") {
            throw MalformedArgumentException(str, "boolean value (true/false)")
        }

        return str.toBooleanStrict()
    }

    override fun suggest(
        input: String,
        sender: CommandSender
    ): List<String> {
        return listOf("true", "false").filter {
            it.startsWith(input.lowercase())
        }
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}true${ChatColor.RESET}|${ChatColor.DARK_PURPLE}false${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}true${ChatColor.RESET}|${ChatColor.LIGHT_PURPLE}false${ChatColor.RESET}>"
}