package me.spartacus04.colosseum.commandHandling.exceptions

/**
 * Exception thrown when a command is in an illegal state.
 *
 * @property key The key representing the illegal state.
 * @param message The detail message for the exception.
 */
class IllegalCommandStateException(val key: String, message: String)
    : Exception(message)