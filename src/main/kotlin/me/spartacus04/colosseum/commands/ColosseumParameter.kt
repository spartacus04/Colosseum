package me.spartacus04.colosseum.commands

import org.bukkit.command.CommandSender

/**
 * Represents a parameter for a command.
 *
 * @property name The name of the parameter.
 * @property optional Whether the parameter is optional.
 */
abstract class ColosseumParameter(val name: String, val optional: Boolean) {

    /**
     * Used to filter a list of strings based on the parameter input.
     *
     * @param parameter The parameter input.
     * @param sender The entity that executed the command.
     * @return The list of completions or null if there are no completions.
     */
    abstract fun onComplete (parameter: String, sender: CommandSender) : List<String>?

    /**
     * Returns the string representation of the parameter. Optional parameters are enclosed in square brackets, while required parameters are enclosed in angle brackets.
     */
    override fun toString(): String {
        return if(optional) {
            "[§5$name§r]"
        } else {
            "<§d$name§r>"
        }
    }
}