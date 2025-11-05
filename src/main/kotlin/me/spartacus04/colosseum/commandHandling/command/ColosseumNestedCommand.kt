package me.spartacus04.colosseum.commandHandling.command

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.commandHandling.argument.arguments.ArgumentString
import me.spartacus04.colosseum.i18n.sendI18nError
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * Represents a nested command that contains sub-commands.
 *
 * @property plugin The ColosseumPlugin instance.
 * @property name The name of the nested command.
 * @property subCommands The list of sub-commands under this nested command.
 */
abstract class ColosseumNestedCommand(val plugin: ColosseumPlugin, val name: String, val subCommands: List<ColosseumCommand>) : ColosseumCommand(plugin) {
    /**
     * Constructs the command descriptor for the nested command. This includes setting up
     * the arguments to handle sub-commands and any additional arguments.
     *
     * @param name The name of the command.
     * @param init A lambda to initialize additional properties of the CommandData.Builder.
     *
     * @return The constructed CommandData for the nested command.
     */
    final override fun commandDescriptor(name: String, init: CommandData.Builder.() -> Unit): CommandData {
        return CommandData.Builder(plugin, name).apply(init).apply {
            this.arguments = mutableListOf(
                ArgumentString(
                    subCommands.map {
                        it.commandData.subCommandName ?: it.commandData.name
                    },
                    false
                ),
                ArgumentString(
                    isGreedy = true
                )
            )

            this.optionalArguments = mutableListOf()
        }.build()
    }

    /**
     * Handles the execution of the nested command by delegating to the appropriate sub-command.
     *
     * @param sender The command sender.
     * @param command The command being executed.
     * @param label The command label.
     * @param args The command arguments.
     */
    final override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val subCommandName = args.getOrNull(0) ?: run {
            sender.sendI18nError(plugin, "error-malformed-argument",
                "expected" to "sub command",
                "at" to label
            )
            return true
        }

        val subCommand = subCommands.find {
            it.commandData.subCommandName == subCommandName || it.commandData.name == subCommandName
        } ?: run {
            sender.sendI18nError(plugin, "error-malformed-argument",
                "expected" to "valid sub command",
                "at" to subCommandName
            )
            return true
        }

        val subLabel = subCommand.commandData.subCommandName ?: subCommand.commandData.name

        subCommand.onCommand(sender, command, subLabel, args.drop(1).toTypedArray())

        return true
    }

    /**
     * Handles tab completion for the nested command by delegating to the appropriate sub-command.
     *
     * @param sender The command sender.
     * @param command The command being executed.
     * @param label The command label.
     * @param args The command arguments.
     *
     * @return A list of possible completions for the command.
     */
    final override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        val subCommandName = args.getOrNull(0) ?: return emptyList()
        val subCommand = subCommands.find {
            it.commandData.subCommandName == subCommandName || it.commandData.name == subCommandName
        } ?: return emptyList()

        val subLabel = subCommand.commandData.subCommandName ?: subCommand.commandData.name

        return subCommand.onTabComplete(sender, command, subLabel, args.drop(1).toTypedArray())
    }
}