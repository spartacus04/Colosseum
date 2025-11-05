package me.spartacus04.colosseum.commandHandling.exceptions

/**
 * Exception thrown when a command is in an illegal state.
 *
 * @property key The key representing the illegal state.
 */
class IllegalCommandStateException(val key: String)
    : Exception("Error of type $key")