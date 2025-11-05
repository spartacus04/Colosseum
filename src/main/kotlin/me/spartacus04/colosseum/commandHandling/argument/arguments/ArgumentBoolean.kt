package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.ChatColor

/**
 * ArgumentBoolean is a parsable argument that represents a boolean value ("true" or "false").
 */
class ArgumentBoolean : ArgumentString(listOf("true", "false"), true) {
    override fun parseComplete(input: List<String>, sender: org.bukkit.command.CommandSender): String {
        val str = input[0].lowercase()

        if (str != "true" && str != "false") {
            throw MalformedArgumentException(str, "boolean value (true/false)")
        }

        return str
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}true${ChatColor.RESET}|${ChatColor.DARK_PURPLE}false${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}true${ChatColor.RESET}|${ChatColor.LIGHT_PURPLE}false${ChatColor.RESET}>"

}