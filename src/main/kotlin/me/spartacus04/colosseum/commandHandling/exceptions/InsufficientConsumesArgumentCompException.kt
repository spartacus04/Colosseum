package me.spartacus04.colosseum.commandHandling.exceptions

/**
 * Exception thrown when there are insufficient arguments consumed in a command.
 *
 * @property got The number of arguments actually consumed.
 * @property expected The number of arguments expected to be consumed.
 * @property at The location in the command where the error occurred.
 */
class InsufficientConsumesArgumentCompException(val got: Int, val expected: Int, val at: String)
    : Exception("Expected at least $expected arguments at $at, got $got.")