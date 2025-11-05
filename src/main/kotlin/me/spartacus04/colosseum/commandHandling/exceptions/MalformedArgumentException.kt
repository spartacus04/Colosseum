package me.spartacus04.colosseum.commandHandling.exceptions

/**
 * Exception thrown when a command argument is malformed.
 *
 * @property at The location in the command where the error occurred.
 * @property expected The expected format or type of the argument.
 */
class MalformedArgumentException(val at: String, val expected: String)
    : Exception("Malformed argument at '$at', expected $expected")