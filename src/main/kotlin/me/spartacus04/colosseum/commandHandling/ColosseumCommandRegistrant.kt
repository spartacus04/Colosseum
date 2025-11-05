package me.spartacus04.colosseum.commandHandling

import me.spartacus04.colosseum.commandHandling.command.ColosseumCommand
import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.commandHandling.command.ColosseumBaseCommand

/**
 * A registrant for Colosseum commands that handles their registration based on server version compatibility.
 *
 * @property plugin The ColosseumPlugin instance.
 */
class ColosseumCommandRegistrant(private val plugin: ColosseumPlugin) {
    /**
     * A mutable list of command classes to be registered.
     */
    val commands: MutableList<Class<out ColosseumCommand>> = mutableListOf()
    val registeredCommands: MutableList<ColosseumCommand> = mutableListOf()

    private var mainCommandName: String? = null

    /**
     * Adds a command class to the registrant.
     *
     * @param command The command class to add.
     * @return The ColosseumCommandRegistrant instance for method chaining.
     */
    fun addCommand(command: Class<out ColosseumCommand>) = apply {
        commands.add(command)
    }

    /**
     * Adds multiple command classes to the registrant.
     *
     * @param commands The command classes to add.
     * @return The ColosseumCommandRegistrant instance for method chaining.
     */
    fun addCommands(vararg commands: Class<out ColosseumCommand>) = apply {
        this.commands.addAll(commands)
    }

    /**
     * Registers a main command that will serve as a parent for all other commands.
     *
     * @param name The name of the main command.
     * @return The ColosseumCommandRegistrant instance for method chaining.
     */
    fun registerMainCommand(name: String) = apply {
        mainCommandName = name
    }

    /**
     * Registers all commands that are compatible with the current server version.
     */
    fun register() {
        val commandCount = commands.size + if(mainCommandName != null) 1 else 0

        commands.forEach { command ->
            if(plugin.serverVersion.isVersionAnnotationCompatible(command)) {
                val commandInst = command.constructors.first { it.parameters.size == 1 }.newInstance(plugin) as ColosseumCommand

                plugin.getCommand(commandInst.commandData.name)!!.setExecutor(commandInst)
                plugin.getCommand(commandInst.commandData.name)!!.tabCompleter = commandInst

                registeredCommands.add(commandInst)
            } else {
                plugin.colosseumLogger.debug("Could not register command ${command.simpleName} due to version incompatibility.")
            }
        }

        var registeredMain = 0

        if(mainCommandName != null) {
            val baseCommand = ColosseumBaseCommand(plugin, mainCommandName!!, registeredCommands)

            plugin.getCommand(baseCommand.commandData.name)!!.setExecutor(baseCommand)
            plugin.getCommand(baseCommand.commandData.name)!!.tabCompleter = baseCommand
            registeredMain = 1
        }

        plugin.colosseumLogger.debug("Registered ${registeredCommands.count() + registeredMain}/${commandCount} commands.")
    }
}