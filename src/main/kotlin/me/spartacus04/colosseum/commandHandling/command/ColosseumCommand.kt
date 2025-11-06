package me.spartacus04.colosseum.commandHandling.command

import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedCommandException
import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.commandHandling.argument.ParsableArgument
import me.spartacus04.colosseum.commandHandling.exceptions.IllegalCommandStateException
import me.spartacus04.colosseum.commandHandling.exceptions.InsufficientConsumesArgumentCompException
import me.spartacus04.colosseum.commandHandling.exceptions.InvalidSenderException
import me.spartacus04.colosseum.i18n.trySendI18nError
import org.bukkit.ChatColor
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Abstract base class for Colosseum commands.
 *
 * @param plugin The ColosseumPlugin instance.
 */
abstract class ColosseumCommand(private val plugin: ColosseumPlugin) : CommandExecutor, TabCompleter {

    /**
     * Data class representing command metadata.
     *
     * @property name The name of the command.
     * @property aliases The set of aliases for the command.
     * @property description The description of the command.
     * @property subCommandName The name of the sub-command, if any.
     * @property permissions The set of permissions required to execute the command.
     * @property arguments The list of required arguments for the command.
     * @property optionalArguments The list of optional arguments for the command.
     */
    data class CommandData (
        val name: String,
        val aliases: Set<String>,
        val description: String,
        val subCommandName: String?,
        val permissions: Set<String>,
        val arguments: List<ParsableArgument<*>>,
        val optionalArguments: List<ParsableArgument<*>>,
        val mcCommand: Command?
    ) {
        /**
         * Builder class for constructing CommandData instances.
         *
         * @param plugin The JavaPlugin instance.
         * @param name The name of the command.
         *
         * @throws IllegalArgumentException if no command with the specified name is found in plugin.yml.
         */
        class Builder(plugin: ColosseumPlugin, private val name: String) {
            /**
             * Initialize the builder by retrieving command metadata from plugin.yml.
             */
            val command = plugin.getCommand("name")

            /**
             * Properties for building CommandData.
             */
            var aliases: Set<String> = command?.aliases?.toMutableSet() ?: emptySet()

            /**
             * The description of the command.
             */
            var description: String = command?.description ?: ""

            /**
             * The name of the sub-command for nested commands usage, if any.
             */
            var subCommandName: String? = null

            /**
             * The set of permissions required to execute the command.
             */
            var permissions: Set<String> = emptySet()

            /**
             * The list of required arguments for the command.
             */
            var arguments: MutableList<ParsableArgument<*>> = mutableListOf()

            /**
             * The list of optional arguments for the command.
             */
            var optionalArguments: MutableList<ParsableArgument<*>> = mutableListOf()

            /**
             * Builds the CommandData instance.
             *
             * @throws IllegalArgumentException if a greedy string argument is not the last argument.
             */
            fun build(): CommandData {
                val argsList = listOf(arguments, optionalArguments).flatten()

                argsList.forEachIndexed { index, arg ->
                    if(arg.consumes < 0 && index != argsList.size - 1) {
                        throw IllegalArgumentException("A greedy string argument must be the last argument.")
                    }
                }

                return CommandData(
                    name,
                    aliases,
                    description,
                    subCommandName,
                    permissions,
                    arguments,
                    optionalArguments,
                    command
                )
            }
        }
    }

    /**
     * Context class for command execution.
     *
     * @param sender The sender of the command.
     * @param args The parsed arguments of the command.
     */
    class CommandContext<T : CommandSender>(
        val sender: T,
        val args: Array<out Any>
    ) {
        /**
         * Gets the argument at the specified index, cast to the expected type.
         *
         * @param index The index of the argument to retrieve.
         * @return The argument at the specified index, cast to type H.
         * @throws IllegalArgumentException if the argument is not of the expected type.
         */
        inline fun <reified H> getArgument(index: Int): H = args[index] as? H
            ?: throw IllegalArgumentException("Argument at index $index is not of type ${H::class.java.simpleName}.")

        /**
         * Gets the number of parsed arguments.
         */
        fun size(): Int = args.size
    }

    /**
     * The command metadata for this Colosseum command.
     */
    abstract val commandData: CommandData

    /**
     * Helper function to create a CommandData instance using a builder pattern.
     *
     * @param name The name of the command.
     * @param init An optional initialization block to configure the CommandData.Builder.
     * @return The constructed CommandData instance.
     */
    protected open fun commandDescriptor(name: String, init: CommandData.Builder.() -> Unit = {}): CommandData {
        return CommandData.Builder(plugin, name).apply(init).build()
    }

    /**
     * Parses the command arguments.
     *
     * @param args The raw command arguments.
     * @param sender The sender of the command.
     * @return An array of parsed arguments.
     *
     * @throws MalformedCommandException if the number of arguments is insufficient.
     * @throws MalformedArgumentException if an argument cannot be parsed.
     */
    private fun parseCommands(args: Array<out String>, sender: CommandSender): Array<out Any> {
        if(args.size < commandData.arguments.sumOf { it.consumes }) {
            throw MalformedCommandException(args.size, commandData.arguments.sumOf { it.consumes })
        }

        val toParse = args.toMutableList()

        fun handleConsumes(arg: ParsableArgument<*>): Int {
            return if (arg.consumes < 0) toParse.size else arg.consumes
        }

        val parsed = commandData.arguments.map { arg ->
            if(toParse.isEmpty()) {
                throw MalformedCommandException(args.size, commandData.arguments.sumOf { it.consumes })
            }

            // For greedy arguments (consumes < 0), consume all remaining items
            val consumes = handleConsumes(arg)

            val slice = toParse.slice(0 until consumes)
            val arg = arg.parse(slice, sender)!!

            toParse.subList(0, consumes).clear()

            arg
        }

        val parsedOptional = commandData.optionalArguments.map {
            if(toParse.isEmpty()) {
                return@map null
            }

            // For greedy arguments (consumes < 0), consume all remaining items
            val consumes = handleConsumes(it)

            val slice = toParse.slice(0 until consumes)
            val arg = it.parse(slice, sender)!!

            toParse.subList(0, consumes).clear()

            arg
        }

        if(toParse.isNotEmpty()) {
            throw MalformedCommandException(args.size, commandData.arguments.sumOf { it.consumes } + commandData.optionalArguments.sumOf { it.consumes })
        }

        return (parsed + parsedOptional.filterNotNull()).toTypedArray()
    }

    /**
     * Generates the command format string for display.
     *
     * @param label The command label.
     * @return The command format string.
     */
    fun getCommandFormat(label: String): String {
        val argsList = listOf(commandData.arguments, commandData.optionalArguments).flatten()

        val argsFormat = argsList.mapIndexed { index, argument ->
            argument.getParamFormat(index >= commandData.arguments.size)
        }.joinToString(" ")

        return "/${ChatColor.AQUA}${label}${ChatColor.RESET} $argsFormat".trimEnd()
    }

    /**
     * Handles command execution. If the command throws an exception during parsing or execution,
     * an appropriate error message is sent to the sender, along with the correct command format.
     *
     * @param sender The sender of the command.
     * @param command The command being executed.
     * @param label The alias of the command used.
     * @param args The raw command arguments.
     *
     * @return true if the command was handled, false otherwise.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val doesNotHaveAllPerms = commandData.permissions.any { !sender.hasPermission(it) }

        if(doesNotHaveAllPerms) {
            sender.trySendI18nError(plugin, "error-no-permission", "You do not have permission to use this command.")
            return true
        }

        try {
            val parsed = parseCommands(args, sender)

            if (execute(
                    CommandContext(sender, parsed)
                ) == null
            ) {
                when (sender) {
                    is Player -> executePlayer(CommandContext(sender, parsed))
                    is ConsoleCommandSender -> executeConsole(CommandContext(sender, parsed))
                    is BlockCommandSender -> executeCommandBlock(CommandContext(sender, parsed))
                }
            }

            return true
        } catch (exception: MalformedCommandException) {
            sender.trySendI18nError(plugin, "error-malformed-command", exception.message!!,
                "got" to exception.got.toString(),
                "expected" to exception.expected.toString()
            )
        } catch (exception: MalformedArgumentException) {
            sender.trySendI18nError(plugin, "error-malformed-argument", exception.message!!,
                "expected" to exception.expected,
                "at" to exception.at
            )
        } catch (exception: InsufficientConsumesArgumentCompException) {
            sender.trySendI18nError(plugin, "error-insufficient-consumes", exception.message!!,
                "expected" to exception.expected.toString(),
                "at" to exception.at,
                "got" to exception.got.toString()
            )
        } catch (exception: InvalidSenderException) {
            sender.trySendI18nError(plugin, "error-invalid-sender-at", exception.message!!,
                "sender" to exception.sender,
                "at" to exception.at
            )
        } catch (exception: IllegalCommandStateException) { // error-no-players, error-no-entities
            sender.trySendI18nError(plugin, exception.key, exception.message!!)
        }

        sender.sendMessage(getCommandFormat(label))

        return true
    }

    /**
     * Handles tab completion for the command.
     *
     * @param sender The sender of the command.
     * @param command The command being completed.
     * @param label The alias of the command used.
     * @param args The raw command arguments.
     */
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String>? {
        val allArgs = listOf(commandData.arguments, commandData.optionalArguments).flatten()

        var argIndex = 0
        var argPosition = 0

        for (arg in allArgs) {
            // For greedy arguments, check if we've reached this position
            if (arg.consumes < 0) {
                if (args.size >= argPosition) {
                    argIndex = allArgs.indexOf(arg)
                }
                break
            }

            val consumes = arg.consumes

            if (args.size - argPosition <= consumes) {
                break
            }

            argPosition += consumes
            argIndex++
        }

        val currentArg = allArgs.getOrNull(argIndex) ?: return emptyList()

        // For greedy arguments, provide all remaining args; otherwise limit by consumes
        val slice = if (currentArg.consumes < 0) {
            args.drop(argPosition)
        } else {
            args.slice(argPosition until minOf(argPosition + currentArg.consumes, args.size))
        }

        return currentArg.suggest(slice, sender)
    }

    /**
     * Executes the command for a player sender.
     *
     * @param ctx The command context.
     */
    open fun executePlayer(ctx: CommandContext<Player>) {
        ctx.sender.trySendI18nError(plugin, "error-invalid-sender", "Error: This command cannot be executed by players.",
            "sender" to "player"
        )
    }

    /**
     * Executes the command for a console sender.
     *
     * @param ctx The command context.
     */
    open fun executeConsole(ctx: CommandContext<ConsoleCommandSender>) {
        ctx.sender.trySendI18nError(plugin, "error-invalid-sender", "Error: This command cannot be executed by the console.",
            "sender" to "console"
        )
    }

    /**
     * Executes the command for a command block sender.
     *
     * @param ctx The command context.
     */
    open fun executeCommandBlock(ctx: CommandContext<BlockCommandSender>) {
        ctx.sender.trySendI18nError(plugin, "error-invalid-sender", "Error: This command cannot be executed by command blocks.",
            "sender" to "command block"
        )
    }

    /**
     * Executes the command.
     *
     * @param ctx The command context.
     */
    open fun execute(ctx: CommandContext<CommandSender>): Unit? = null
}