package me.spartacus04.colosseum.commands

import me.spartacus04.colosseum.i18n.ColosseumI18nManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents a registry for commands that can be registered.
 */
class ColosseumCommandRegistry(private val plugin: JavaPlugin, private val i18nManager: ColosseumI18nManager, private val mainCommand: String) {
    private val commands = arrayListOf<ColosseumExecutor>()

    /**
     * Adds a command to the registry.
     *
     * @param commandExecutor The executor for the command.
     * @param commandString The name of the command.
     * @param subCommandString The name of the subcommand for the main command. Defaults to the command name.
     *
     * @return The registry.
     */
    fun addCommand(commandExecutor: Class<out ColosseumExecutor>, commandString: String, subCommandString: String = commandString) : ColosseumCommandRegistry {
        commands.add(
            commandExecutor.getConstructor(
                String::class.java,
                String::class.java,
                JavaPlugin::class.java,
                ColosseumI18nManager::class.java
            ).newInstance(commandString, subCommandString, plugin, i18nManager)
        )

        return this
    }

    /**
     * Returns the list of commands in the registry.
     *
     * @return The list of commands.
     */
    fun getCommands(): List<ColosseumExecutor> {
        return commands
    }

    /**
     * Returns a command from the registry.
     *
     * @param name The name of the command.
     * @return The command or null if the command does not exist.
     */
    fun getCommand(name: String): ColosseumExecutor? {
        return commands.find { it.subCommandString == name }
    }

    /**
     * Registers the commands in the registry.
     * This should be called after all commands have been added.
     * This will register the main command and all subcommands.
     */
    fun registerCommands() {
        commands.forEach {
            it.register()
        }

        ColosseumMainCommand(mainCommand, plugin, i18nManager, commands).register()
    }
}