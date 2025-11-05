package me.spartacus04.colosseum.commandHandling.exceptions

/**
 * Exception thrown when a command is malformed due to incorrect number of arguments.
 *
 * @property got The number of arguments received.
 * @property expected The number of arguments expected.
 */
class MalformedCommandException(val got: Int, val expected: Int)
    : Exception("Malformed command: expected $expected arguments, got $got.")