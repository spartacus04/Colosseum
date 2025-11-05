package me.spartacus04.colosseum.commandHandling.argument.arguments

import me.spartacus04.colosseum.commandHandling.argument.Argument
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.scoreboard.Team

/**
 * ArgumentTeam is a parsable argument that represents a scoreboard team.
 * It suggests valid team names for auto-completion.
 */
class ArgumentTeam : Argument<Team>() {
    override fun parse(input: String, sender: CommandSender): Team {
        return sender.server.scoreboardManager.mainScoreboard.getTeam(input)
            ?: throw MalformedArgumentException(input, "team")
    }

    override fun suggest(
        input: String,
        sender: CommandSender
    ): List<String> {
        return sender.server.scoreboardManager.mainScoreboard.teams.map { it.name }.filter { it.startsWith(input) }
    }

    override fun getParamFormat(isOptional: Boolean): String =
        if (isOptional)
            "${ChatColor.RESET}[${ChatColor.DARK_PURPLE}team${ChatColor.RESET}]"
        else
            "${ChatColor.RESET}<${ChatColor.LIGHT_PURPLE}team${ChatColor.RESET}>"
}