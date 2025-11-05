package me.spartacus04.colosseum.commandHandling.argument

import org.bukkit.command.CommandSender

/**
 * Represents a parsable argument that consumes a single input string.
 */
abstract class Argument<T> : ParsableArgument<T>() {
    override val consumes = 1

    /**
     * Parses the input string into the desired type.
     *
     * @param input The list of input strings.
     * @param sender The command sender.
     * @return The parsed argument of type T.
     */
    final override fun parse(input: List<String>, sender: CommandSender): T
        = parse(input[0], sender)

    /**
     * Suggests possible completions for the input string.
     *
     * @param input The list of input strings.
     * @param sender The command sender.
     * @return A list of suggested completions.
     */
    final override fun suggest(input: List<String>, sender: CommandSender): List<String>
        = suggest(input[0], sender)

    /**
     * Parses the input string into the desired type.
     *
     * @param input The input string.
     * @param sender The command sender.
     * @return The parsed argument of type T.
     */
    abstract fun parse(input: String, sender: CommandSender): T

    /**
     * Suggests possible completions for the input string.
     *
     * @param input The input string.
     * @param sender The command sender.
     * @return A list of suggested completions.
     */
    abstract fun suggest(input: String, sender: CommandSender): List<String>
}