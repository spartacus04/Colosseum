package me.spartacus04.colosseum.commands.parameters

import me.spartacus04.colosseum.commands.ColosseumParameter
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Represents a parameter for a player.
 *
 * @param optional Whether the parameter is optional.
 * @param name The name of the parameter.
 */
class ParameterPlayer(optional: Boolean, name: String = "player") : ColosseumParameter(name, optional) {
    override fun onComplete(parameter: String, sender: CommandSender): List<String>? {
        val players = Bukkit.getOnlinePlayers().toList().map { it.name }
        val playersAndSelectors = if(sender !is Player) {
            listOf("@a", "@r").plus(players)
        }
        else {
            listOf("@a", "@r", "@s").plus(players)
        }

        val matches = playersAndSelectors.filter { it.startsWith(parameter, true) }

        return matches.ifEmpty { null }
    }

    companion object {
        /**
         * Returns player(s) from the given selector. Handles @a, @r, @s and player names.
         *
         * @param selector The selector to use.
         * @param sender The entity that executed the command.
         *
         * @return The player(s) or an empty list if the selector is invalid.
         */
        fun getPlayerFromSelector(selector: String, sender: CommandSender): List<Player> {
            return when(selector) {
                "@a" -> Bukkit.getOnlinePlayers().toList()
                "@r" -> Bukkit.getOnlinePlayers().random().let { listOf(it) }
                "@s" -> if(sender is Player) listOf(sender) else emptyList()
                else -> Bukkit.getPlayer(selector)?.let { listOf(it) } ?: emptyList()
            }
        }
    }
}