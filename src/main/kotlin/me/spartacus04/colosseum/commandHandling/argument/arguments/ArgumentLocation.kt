package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.argument.ArgumentComp
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import me.spartacus04.colosseum.utils.addLocal
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

/**
 * ArgumentLocation is a parsable argument that represents a location in the game world.
 * It supports absolute, relative (~), and local (^) coordinates.
 */
class ArgumentLocation : ArgumentComp<Location>(3) {
    internal enum class LocationPartType {
        ABSOLUTE,
        RELATIVE,
        LOCAL
    }

    internal class ArgumentLocationPart : Argument<Pair<LocationPartType, Double>>() {
        override fun parse(
            input: String,
            sender: CommandSender
        ): Pair<LocationPartType, Double> {
            val locationPartType = if(sender is ConsoleCommandSender) {
                LocationPartType.ABSOLUTE
            } else if(input.startsWith("~")) {
                LocationPartType.RELATIVE
            } else if(input.startsWith("^")) {
                LocationPartType.LOCAL
            } else {
                LocationPartType.ABSOLUTE
            }

            val noPrefix = input.removePrefix("^").removePrefix("~")

            val value = if(noPrefix.isEmpty()) {
                0.0
            } else {
                noPrefix.toDoubleOrNull() ?: throw MalformedArgumentException(input, "coordinate")
            }

            return Pair(locationPartType, value)
        }

        override fun suggest(
            input: String,
            sender: CommandSender
        ): List<String> {
            return if(sender is ConsoleCommandSender) {
                emptyList()
            } else if(input.isEmpty()) {
                listOf("~")
            } else {
                listOf(input)
            }
        }

        override fun getParamFormat(isOptional: Boolean) = ""
    }


    override fun parseComplete(input: List<String>, sender: CommandSender): Location {
        val locValues = input.map { ArgumentLocationPart().parse(it, sender) }

        val baseLocation = if(sender is Player) {
            sender.location
        } else if(sender is BlockCommandSender) {
            sender.block.location
        } else {
            Location(
                Bukkit.getWorld("world"),
                0.0, 0.0, 0.0,
                0f, 0f
            )
        }

        // Handle local coordinates
        if(locValues.any { it.first == LocationPartType.LOCAL }) {
            if(!locValues.all { it.first == LocationPartType.LOCAL }) {
                throw MalformedArgumentException(input.joinToString(" "), "local coordinates")
            }

            return baseLocation.addLocal(locValues[0].second, locValues[1].second, locValues[2].second)
        }

        baseLocation.x = if(locValues[0].first == LocationPartType.RELATIVE) {
            locValues[0].second + baseLocation.x
        } else {
            locValues[0].second
        }

        baseLocation.y = if(locValues[1].first == LocationPartType.RELATIVE) {
            locValues[1].second + baseLocation.y
        } else {
            locValues[1].second
        }

        baseLocation.z = if(locValues[2].first == LocationPartType.RELATIVE) {
            locValues[2].second + baseLocation.z
        } else {
            locValues[2].second
        }

        return baseLocation
    }

    override fun suggest(input: List<String>, sender: CommandSender): List<String> {
        if(sender is Player) {
            val targetedBlock = sender.getTargetBlockExact(4)

            if(targetedBlock != null) {
                return listOf(when(input.size) {
                    1 -> targetedBlock.x.toString()
                    2 -> targetedBlock.y.toString()
                    3 -> targetedBlock.z.toString()
                    else -> throw IllegalStateException("Invalid location part: $input")
                })
            }
        }

        return ArgumentLocationPart().suggest(input.last(), sender)
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}x${ChatColor.RESET}] [${ChatColor.DARK_PURPLE}y${ChatColor.RESET}] [${ChatColor.DARK_PURPLE}z${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}x${ChatColor.RESET}> <${ChatColor.LIGHT_PURPLE}x${ChatColor.RESET}> <${ChatColor.LIGHT_PURPLE}x${ChatColor.RESET}>"
}