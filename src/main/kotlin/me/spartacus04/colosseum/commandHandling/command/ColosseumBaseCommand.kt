package me.spartacus04.colosseum.commandHandling.command

import me.spartacus04.colosseum.ColosseumPlugin

/**
 * Base command class for Colosseum commands. This class serves as the root command that can contain multiple sub-commands.
 *
 * @param plugin The Colosseum plugin instance.
 * @param name The name of the base command.
 * @param subCommands A list of sub-commands under this base command.
 */
class ColosseumBaseCommand(plugin: ColosseumPlugin, name: String, subCommands: List<ColosseumCommand>) : ColosseumNestedCommand(plugin, name, subCommands) {
    override val commandData = commandDescriptor(name) { }
}