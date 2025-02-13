package me.spartacus04.colosseum.commands.parameters

import me.spartacus04.colosseum.commands.ColosseumParameter
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * Represents a parameter for a location. Handles relative, local and absolute coordinates.
 */
class ParameterLocation(val locationType: LocationType, optional: Boolean, name: String = "location$locationType"): ColosseumParameter(name, optional) {
    enum class LocationType {
        X,
        Y,
        Z
    }

    override fun onComplete(parameter: String, sender: CommandSender): List<String> {
        val suggestions = mutableListOf<String>()

        suggestions.add("~")
        suggestions.add("^")

        val player = (sender as? Player) ?: return emptyList()
        val block = player.getTargetBlockExact(4) ?: return suggestions

        when(locationType) {
            LocationType.X -> {
                suggestions.add(block.x.toString())
            }
            LocationType.Y -> {
                suggestions.add(block.y.toString())
            }
            LocationType.Z -> {
                suggestions.add(block.z.toString())
            }
        }

        return suggestions
    }

    companion object {
        /**
         * Returns a location from the given coordinates.
         * @param coordinates The coordinates to use.
         * @param target The player to use as a reference.
         * @return The location or null if the coordinates are invalid
         */
        fun getLocationFromSelector(coordinates: Array<String>, target: Player): Location? {
            if(coordinates.size != 3) {
                return null
            }

            if(coordinates.any { it.startsWith("^") }) {
                if(!coordinates.all { it.startsWith("^") }) {
                    return null
                }

                val coords = coordinates.map { it.substring(1).toDouble() }
                val direction = target.location.direction

                val leftApplied = direction.clone().rotateAroundY(Math.toRadians(90.0)).multiply(coords[0])
                val upApplied = direction.clone().add(Vector(0.0, coords[1], 0.0))
                val forwardApplied = direction.clone().setY(0).multiply(coords[2])

                val relativeVector = leftApplied.add(upApplied).add(forwardApplied)
                return target.location.clone().add(relativeVector)
            }

            val coords = coordinates.mapIndexed { index, it ->
                if(it.startsWith("~")) {
                    return@mapIndexed when(index) {
                        0 -> target.location.x + if(it.length == 1) 0.0 else it.substring(1).toDouble()
                        1 -> target.location.y + if(it.length == 1) 0.0 else it.substring(1).toDouble()
                        2 -> target.location.z + if(it.length == 1) 0.0 else it.substring(1).toDouble()
                        else -> return null
                    }
                }

                return@mapIndexed it.toDouble()
            }

            return Location(target.world, coords[0], coords[1], coords[2])
        }
    }
}