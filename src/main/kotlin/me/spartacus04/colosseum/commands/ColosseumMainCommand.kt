package me.spartacus04.colosseum.commands

import me.spartacus04.colosseum.i18n.ColosseumI18nManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

internal class ColosseumMainCommand internal constructor(commandName: String, plugin: JavaPlugin, i18nManager: ColosseumI18nManager, private val commands: List<ColosseumExecutor>) : ColosseumExecutor(commandName, commandName, plugin, i18nManager) {
    override val parameters: List<ColosseumParameter>
        get() = emptyList()

    override fun execute(sender: CommandSender, args: Array<out String>) {
        if(args.isEmpty()) {
            sender.sendMessage(ColosseumI18nManager.replacePlaceholders(
                i18nManager.messageFormatter.confirm("v%version%")
            , mapOf(
                "version" to plugin.description.version
            )))

            return
        }

        val subArgs = try {
            args.copyOfRange(1, args.size)
        } catch (_ : Exception) {
            arrayOf()
        }

        val subcommand = commands.find { it.subCommandString == args[0] } ?: return

        Bukkit.dispatchCommand(sender, "${subcommand.commandString} ${subArgs.joinToString(separator = " ")}")

        return
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String>? {
        if(args.size == 1) {
            return commands.map { it.subCommandString }
        }

        val commandExecutor = commands.find {
            it.subCommandString == args[0]
        } ?: return null

        val subArgs = args.copyOfRange(1, args.size)
        return commandExecutor.onTabComplete(sender, command, commandExecutor.commandString, subArgs)
    }
}