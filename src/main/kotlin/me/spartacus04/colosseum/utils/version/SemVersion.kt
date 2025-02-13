package me.spartacus04.colosseum.utils.version

/**
 * Represents a semantic version.
 *
 * @property versions The version components.
 */
open class SemVersion(private val versions: List<Int>) {

    /**
     * Creates a new [SemVersion] from a version string.
     *
     * @param version The version string.
     */
    constructor(version: String) : this(version.split(".").map { it.toInt() })

    /**
     * Compares this version to another version. If the number of components in the versions differ, the version with fewer components is considered less than the other.
     *
     * @param other The other version.
     * @return A negative integer, zero, or a positive integer if this version is less than, equal to, or greater than the other version.
     */
    operator fun compareTo(other: SemVersion): Int {
        for (i in 0 until minOf(versions.size, other.versions.size)) {
            val comparison = versions[i].compareTo(other.versions[i])
            if (comparison != 0) return comparison
        }

        return versions.size.compareTo(other.versions.size)
    }

    /**
     * Compares this version to another version. If the number of components in the versions differ, the version with fewer components is considered less than the other.
     *
     * @param other The other version.
     * @return A negative integer, zero, or a positive integer if this version is less than, equal to, or greater than the other version.
     */
    operator fun compareTo(other: String) = compareTo(SemVersion(other))

    /**
     * Returns the string representation of this version.
     *
     * @return The string representation of this version.
     */
    override fun toString(): String {
        return versions.joinToString(".")
    }
}