package me.spartacus04.colosseum.commandHandling.exceptions

import org.bukkit.ChatColor

/**
 * Exception thrown when a command is malformed due to incorrect number of arguments.
 *
 * @property got The number of arguments received.
 * @property expected The number of arguments expected.
 */
class MalformedCommandException(val got: Int, val expected: Int)
    : Exception("${ChatColor.RED}Malformed command: expected '${ChatColor.YELLOW}$expected${ChatColor.RED}' arguments, got '${ChatColor.YELLOW}$got${ChatColor.RED}'.")