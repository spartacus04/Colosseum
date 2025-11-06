package me.spartacus04.colosseum.commandHandling.exceptions

import org.bukkit.ChatColor

/**
 * Exception thrown when there are insufficient arguments consumed in a command.
 *
 * @property got The number of arguments actually consumed.
 * @property expected The number of arguments expected to be consumed.
 * @property at The location in the command where the error occurred.
 */
class InsufficientConsumesArgumentCompException(val got: Int, val expected: Int, val at: String)
    : Exception("${ChatColor.RED}Error: Expected at least ${ChatColor.YELLOW}'$expected'${ChatColor.RED} arguments at ${ChatColor.YELLOW}'$at'${ChatColor.RED}, got '${ChatColor.YELLOW}$got'${ChatColor.RED}.")