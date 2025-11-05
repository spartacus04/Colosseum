package me.spartacus04.colosseum.commandHandling.argument

import me.spartacus04.colosseum.commandHandling.exceptions.IllegalCommandStateException
import me.spartacus04.colosseum.commandHandling.exceptions.InsufficientConsumesArgumentCompException
import me.spartacus04.colosseum.commandHandling.exceptions.InvalidSenderException
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.command.CommandSender
import kotlin.jvm.Throws

/**
 * Represents a parsable argument for command handling.
 *
 * @param T The type of the parsed argument.
 */
abstract class ParsableArgument<T> {
    /**
     * The number of input strings this argument consumes.
     */
    abstract val consumes: Int

    /**
     * Parses the input strings into the desired type.
     *
     * @param input The list of input strings.
     * @param sender The command sender.
     * @return The parsed argument of type T.
     */
    @Throws(MalformedArgumentException::class, InsufficientConsumesArgumentCompException::class,
        IllegalCommandStateException::class, InvalidSenderException::class)
    abstract fun parse(input: List<String>, sender: CommandSender): T

    /**
     * Suggests possible completions for the input strings.
     *
     * @param input The list of input strings.
     * @param sender The command sender.
     * @return A list of suggested completions.
     */
    abstract fun suggest(input: List<String>, sender: CommandSender): List<String>

    /**
     * Gets the usage string for this argument.
     *
     * @param isOptional Whether the argument is optional.
     * @return The usage string.
     */
    abstract fun getParamFormat(isOptional: Boolean): String
}