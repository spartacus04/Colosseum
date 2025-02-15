package me.spartacus04.colosseum.commands

import me.spartacus04.colosseum.i18n.ColosseumI18nManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents a command executor. Tab completion is handled automatically based on the parameters.
 *
 * @property commandString The name of the command.
 * @property subCommandString The name of the subcommand for the main command. Defaults to the command name.
 * @property plugin The plugin that the command is registered to.
 * @property i18nManager The i18n manager for the plugin.
 */
abstract class ColosseumExecutor(val commandString: String, val subCommandString: String = commandString, val plugin: JavaPlugin, val i18nManager: ColosseumI18nManager) : CommandExecutor, TabCompleter {

    /**
     * The parameters for the command.
     */
    abstract val parameters: List<ColosseumParameter>

    /**
     * Registers the command to the plugin.
     */
    fun register() {
        val command = plugin.getCommand(commandString)

        if(command == null) {
            i18nManager.error("Failed to register command: $commandString")
            return
        }

        command.setExecutor(this)
        command.tabCompleter = this

        i18nManager.debug("Registered command: $commandString")
    }

    /**
     * Handles tab completion based on the parameters.
     * @param sender The entity that executed the command.
     * @param command The command that was executed.
     * @param label The label of the command.
     * @param args The arguments of the command.
     *
     * @return The list of completions or null if there are no completions.
     */
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String>? {
        return try {
            val index = args.size - 1
            val parameter = parameters[index]
            parameter.onComplete(args[index], sender)
        } catch(_: IndexOutOfBoundsException) {
            null
        }
    }

    /**
     * Handles command execution based on the parameters. Language handling is done automatically with the i18n manager. You may need to set `missing-permission` and `command-usage` in your language file.
     * @param sender The entity that executed the command.
     * @param command The command that was executed.
     * @param label The label of the command.
     * @param args The arguments of the command.
     *
     * @return Whether the command has to show its usage.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.testPermissionSilent(sender)) {
            sender.sendMessage(i18nManager.messageFormatter.error(i18nManager.getFormatted(sender, "missing-permission")))
            return true
        }

        if(args.size < parameters.size) {
            if(args.isEmpty() || !parameters[args.size - 1].optional) {
                if(!parameters[args.size].optional) {
                    sender.sendMessage(i18nManager.messageFormatter.info(i18nManager.getFormatted(
                        sender,
                        "command-usage",
                        hashMapOf(
                            "command" to "$commandString${parameters.map { " $it" }.joinToString()}",
                        )
                    )))
                    return true
                }
            }
        }

        if(execute(sender, args) == null) {
            if(sender is Player) {
                executePlayer(sender, args)
            } else if(sender is ConsoleCommandSender) {
                executeConsole(sender, args)
            }
        }

        return true
    }

    /**
     * Executes the command for a player.
     * @param sender The player that executed the command.
     * @param args The arguments of the command.
     */
    open fun executePlayer(sender: Player, args: Array<out String>) {
        sender.sendMessage(
            i18nManager.messageFormatter.error(
                i18nManager.getFormatted(
                    sender,
                    "invalid-runner",
                    hashMapOf(
                        "runner" to "console"
                    )
                )
            )
        )
    }

    /**
     * Executes the command for the console.
     * @param sender The console that executed the command.
     * @param args The arguments of the command.
     */
    open fun executeConsole(sender: ConsoleCommandSender, args: Array<out String>) {
        sender.sendMessage(
            i18nManager.messageFormatter.error(
                i18nManager.getFormatted(
                    sender,
                    "invalid-runner",
                    hashMapOf(
                        "runner" to "player"
                    )
                )
            )
        )
    }

    /**
     * Executes the command for both players and the console.
     * @param sender The entity that executed the command.
     * @param args The arguments of the command.
     */
    open fun execute(sender: CommandSender, args: Array<out String>): Unit? = null
}