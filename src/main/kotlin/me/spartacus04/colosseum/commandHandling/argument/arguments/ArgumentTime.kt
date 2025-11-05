package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * ArgumentTime is a parsable argument that represents a time duration.
 * It supports suffixes 's' for seconds, 'd' for days, and 't' for ticks.
 */
class ArgumentTime : Argument<Int>() {
    override fun parse(input: String, sender: CommandSender): Int {
        val multiplier = when {
            input.endsWith("s") -> 20
            input.endsWith("d") -> 24000
            input.endsWith("t") -> 1
            else -> 1
        }

        val numberPart = input.removeSuffix("s").removeSuffix("d").removeSuffix("t")

        val number = numberPart.toIntOrNull() ?: throw MalformedArgumentException(input, "time")

        return number * multiplier
    }

    override fun suggest(
        input: String,
        sender: CommandSender
    ): List<String> {
        if(!input.endsWith("d") && !input.endsWith("s") && !input.endsWith("t")) {
            return if(input.isEmpty()) {
                listOf("1d", "1s", "1t")
            } else {
                listOf("d", "s", "t").map { input + it }
            }
        }

        return emptyList()
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}time${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}time${ChatColor.RESET}>"
}
