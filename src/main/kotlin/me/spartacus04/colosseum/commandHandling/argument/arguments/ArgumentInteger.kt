package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * ArgumentInteger is a parsable argument that represents an integer number.
 *
 * @param suggestedValues A list of suggested integer values for auto-completion.
 */
class ArgumentInteger(private val suggestedValues: List<Int> = emptyList()) : Argument<Int>() {
    override fun parse(input: String, sender: CommandSender): Int {
        return input.toIntOrNull() ?: throw MalformedArgumentException(input, "Integer")
    }

    override fun suggest(input: String, sender: CommandSender): List<String> {
        return suggestedValues.map { it.toString() }.filter { it.startsWith(input) }
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}integer${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}integer${ChatColor.RESET}>"
}