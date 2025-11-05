package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * ArgumentPlayers is a parsable argument that represents one or more players.
 *
 * @param singlePlayer If true, only a single player can be specified.
 */
class ArgumentPlayers(singlePlayer: Boolean = false) : ArgumentEntities(singleEntity = singlePlayer, onlyPlayers = true) {
    override fun parse(
        input: String,
        sender: CommandSender
    ): List<Player> {
        return super.parse(input, sender).map {
            it as? Player ?: throw MalformedArgumentException(input, "player${if(singleEntity) "" else "s"}")
        }
    }
}