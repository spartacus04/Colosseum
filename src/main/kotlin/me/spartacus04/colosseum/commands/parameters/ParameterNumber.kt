package me.spartacus04.colosseum.commands.parameters

import me.spartacus04.colosseum.commands.ColosseumParameter
import org.bukkit.command.CommandSender

/**
 * Represents a parameter for a number.
 *
 * @param optional Whether the parameter is optional.
 * @param suggestedValues The suggested values for the parameter.
 * @param name The name of the parameter.
 */
class ParameterNumber(optional: Boolean, vararg suggestedValues: Number, name: String = "number") : ColosseumParameter(name, optional) {
    private val suggestedValues = suggestedValues.map { it.toString() }

    override fun onComplete(parameter: String, sender: CommandSender): List<String>? {
        return if (parameter.isEmpty()) {
            suggestedValues
        } else {
            suggestedValues.filter { it.startsWith(parameter) }
        }
    }
}