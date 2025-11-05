package me.spartacus04.colosseum.utils

import org.bukkit.Location
import org.bukkit.util.Vector

/**
 * Adds a local vector to the location, taking into account the location's yaw and pitch.
 *
 * @param local The local vector to add.
 * @return The new location.
 */
fun Location.addLocal(local: Vector) : Location {
    val axisBase = Vector(0, 0, 1)

    val axisLeft = axisBase.clone().rotateAroundY(Math.toRadians(-this.yaw + 90.0))

    val axisUp = this.direction.clone().rotateAroundX(Math.toRadians(-this.x)).rotateAroundNonUnitAxis(axisLeft, Math.toRadians(-90.0))

    val sway = axisLeft.clone().normalize().multiply(local.x)
    val heave = axisUp.clone().normalize().multiply(local.y)
    val surge = this.direction.clone().normalize().multiply(local.z)

    return this.clone().add(sway).add(heave).add(surge)
}

/**
 * Adds a local location to the location, taking into account the location's yaw and pitch.
 *
 * @param location The local location to add.
 * @return The new location.
 */
fun Location.addLocal(location: Location)
    = this.addLocal(location.toVector())

/**
 * Adds local x, y, z coordinates to the location, taking into account the location's yaw and pitch.
 *
 * @param x The local x coordinate to add.
 * @param y The local y coordinate to add.
 * @param z The local z coordinate to add.
 * @return The new location.
 */
fun Location.addLocal(x: Double, y: Double, z: Double)
    = this.addLocal(Vector(x, y, z))

