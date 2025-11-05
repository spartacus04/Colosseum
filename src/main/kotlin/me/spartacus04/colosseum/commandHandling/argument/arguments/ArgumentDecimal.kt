package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * ArgumentDecimal is a parsable argument that represents a decimal number.
 *
 * @param suggestedValues A list of suggested decimal values for auto-completion.
 */
class ArgumentDecimal(private val suggestedValues: List<Double> = emptyList()) : Argument<Double>() {
    override fun parse(input: String, sender: CommandSender): Double {
        return input.toDoubleOrNull() ?: throw MalformedArgumentException(input, "Decimal number")
    }

    override fun suggest(input: String, unused: CommandSender): List<String> {
        return suggestedValues.map { it.toString() }.filter { it.startsWith(input) }
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}decimal${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}decimal${ChatColor.RESET}>"
}