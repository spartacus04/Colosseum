package me.spartacus04.colosseum.commands.parameters

import me.spartacus04.colosseum.commands.ColosseumParameter
import org.bukkit.command.CommandSender

class ParameterString(val suggestedValues: List<String>, name: String = "text"): ColosseumParameter(name, false) {

    override fun onComplete(parameter: String, sender: CommandSender): List<String>? {
        return if (parameter.isEmpty()) {
            suggestedValues
        } else {
            suggestedValues.filter { it.startsWith(parameter) }
        }
    }
}