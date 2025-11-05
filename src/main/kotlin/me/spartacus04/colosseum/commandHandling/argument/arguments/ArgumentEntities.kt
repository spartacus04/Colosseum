package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.exceptions.IllegalCommandStateException
import me.spartacus04.colosseum.commandHandling.exceptions.InvalidSenderException
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/**
 * ArgumentEntities is a parsable argument that represents one or more entities in the game.
 *
 * @param singleEntity If true, only a single entity can be selected.
 * @param onlyPlayers If true, only player entities can be selected.
 */
open class ArgumentEntities(val singleEntity: Boolean = false, val onlyPlayers: Boolean = false) : Argument<List<Entity>>() {
    val suggestedValues = mutableListOf<String>("@p", "@r")

    init {
        if(!onlyPlayers) {
            suggestedValues.add("@n")
            suggestedValues.add("@s")

            if(!singleEntity) {
                suggestedValues.add("@e")
            }
        }

        if(!singleEntity) {
            suggestedValues.add("@a")
        }
    }


    override fun parse(
        input: String,
        sender: CommandSender
    ): List<Entity> {
        val validSelectors = suggest(input, sender)

        if(!validSelectors.contains(input)) {
            throw MalformedArgumentException(input, if(onlyPlayers) {
                if(singleEntity) "player" else "players"
            } else {
                if(singleEntity) "entity" else "entities"
            })
        }

        when(input) {
            "@p" -> if(sender is ConsoleCommandSender) {
                throw InvalidSenderException(input, "console")
            } else if(sender is BlockCommandSender) {
                // select nearest player to the command block
                val players = sender.block.location.world?.players ?: emptyList()

                if(players.isEmpty()) {
                    throw IllegalCommandStateException("error-no-players")
                }

                return listOf(players.minByOrNull { it.location.distanceSquared(sender.block.location) }!!)
            } else {
                return listOf((sender as Player))
            }

            "@r" -> {
                val players = Bukkit.getOnlinePlayers().toList()
                if(players.isEmpty()) {
                    throw IllegalCommandStateException("error-no-players")
                }
                return listOf(players.random())
            }

            "@a" -> return Bukkit.getOnlinePlayers().toList()

            "@s" -> if(sender is Player) {
                return listOf(sender)
            } else {
                throw InvalidSenderException(input, "player")
            }

            "@e" -> return Bukkit.getWorlds().flatMap { it.entities }

            "@n" -> if(sender is ConsoleCommandSender) {
                throw InvalidSenderException(input, "console")
            } else if(sender is BlockCommandSender) {
                val entities = sender.block.location.world?.entities ?: emptyList()

                if(entities.isEmpty()) {
                    throw IllegalCommandStateException("error-no-entities")
                }

                return listOf(entities.minByOrNull { it.location.distanceSquared(sender.block.location) }!!)
            } else {
                return listOf((sender as Player))
            }

            else -> {
                val playerByName = Bukkit.getPlayerExact(input)

                if(playerByName != null) {
                    return listOf(playerByName)
                }

                val entityByUUID = try {
                    Bukkit.getEntity(java.util.UUID.fromString(input))
                } catch (_: IllegalArgumentException) {
                    null
                }

                if(entityByUUID != null) {
                    if(onlyPlayers && entityByUUID !is Player) {
                        throw MalformedArgumentException(input, "player${if(singleEntity) "" else "s"}")
                    }

                    return listOf(entityByUUID)
                }
            }
        }

        return emptyList()
    }

    override fun suggest(
        input: String,
        sender: CommandSender
    ): List<String> {
        val suggested = suggestedValues.toMutableList()
        suggested.addAll(Bukkit.getOnlinePlayers().map { it.name }.toList())

        if (sender is ConsoleCommandSender) {
            suggested.remove("@s")
            suggested.remove("@p")
        } else if (sender is Player) {
            val targetEntity = sender.getTargetEntity(4)

            if (targetEntity != null) {
                if (onlyPlayers && targetEntity is Player) {
                    suggested.add(targetEntity.uniqueId.toString())
                } else if (!onlyPlayers) {
                    suggested.add(targetEntity.uniqueId.toString())
                }
            }
        }

        return suggested.filter { it.startsWith(input) }
    }

    override fun getParamFormat(isOptional: Boolean): String {
        val text = (if(onlyPlayers) "player" else "entity") + (if(singleEntity) "" else "(s)")

        return if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}${text}${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}${text}${ChatColor.RESET}>"
    }
}