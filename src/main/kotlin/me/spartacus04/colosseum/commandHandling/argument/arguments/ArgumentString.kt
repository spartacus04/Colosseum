package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.ArgumentComp
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * ArgumentString is a parsable argument that represents a string.
 *
 * @param suggestions A list of suggested string values for auto-completion.
 * @param isGreedy If true, the argument will consume all remaining input as a single string. A greedy argument is always the last argument.
 */
open class ArgumentString(val suggestions: List<String> = emptyList(), val isGreedy: Boolean = false) : ArgumentComp<String>(if(isGreedy) -1 else 1) {
    override fun parseComplete(
        input: List<String>,
        sender: CommandSender
    ): String {
        return input.joinToString(" ")
    }

    override fun suggest(
        input: List<String>,
        sender: CommandSender
    ): List<String> {
        return suggestions.filter { it.startsWith(input.joinToString(" ")) }
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}string${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}string${ChatColor.RESET}>"
}