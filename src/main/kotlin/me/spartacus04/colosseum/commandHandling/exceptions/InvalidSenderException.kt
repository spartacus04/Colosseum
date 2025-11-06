package me.spartacus04.colosseum.commandHandling.exceptions

import org.bukkit.ChatColor

/**
 * Exception thrown when a command is executed by an invalid sender.
 *
 * @property at The location in the command where the error occurred.
 * @property sender The invalid sender type.
 */
class InvalidSenderException(val at: String, val sender: String)
    : Exception("${ChatColor.RED}Error: Invalid sender '${ChatColor.YELLOW}$sender${ChatColor.RED}' at ${ChatColor.YELLOW}'$at${ChatColor.RED}'.")