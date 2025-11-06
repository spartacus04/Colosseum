package me.spartacus04.colosseum.commandHandling.argument

import me.spartacus04.colosseum.commandHandling.exceptions.InsufficientConsumesArgumentCompException
import org.bukkit.command.CommandSender

/**
 * Represents a parsable argument that consumes multiple input strings.
 *
 * @param consumes The number of input strings this argument consumes.
 */
abstract class ArgumentComp<T>(override val consumes: Int) : ParsableArgument<T>() {
    init {
        require(consumes != 0) { "consumes must be at least 1, or < 0 (greedy)" }
    }

    /**
     * Parses the input strings into the desired type.
     *
     * @param input The list of input strings.
     * @param sender The command sender.
     * @return The parsed argument of type T.
     */
    final override fun parse(input: List<String>, sender: CommandSender): T {
        if(input.size < consumes && consumes > 0)
            throw InsufficientConsumesArgumentCompException(input.size, consumes, input.joinToString(" "))

        // For greedy arguments (consumes < 0), take all input; otherwise take the specified amount
        val toTake = if (consumes < 0) input else input.take(consumes)
        return parseComplete(toTake, sender)
    }

    /**
     * Parses the complete input strings into the desired type.
     *
     * @param input The list of input strings.
     * @param sender The command sender.
     * @return The parsed argument of type T.
     */
    abstract fun parseComplete(input: List<String>, sender: CommandSender): T
}