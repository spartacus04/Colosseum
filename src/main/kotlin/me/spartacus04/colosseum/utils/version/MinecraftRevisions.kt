package me.spartacus04.colosseum.utils.version

/**
 * Enum class representing Net.Minecraft.Server revisions.
 *
 * @property version The version string of the Minecraft revision.
 */
enum class MinecraftRevisions(val version: String) {
    /**
     * 1.21.9 - latest
     */
    R25("1.21.9"),
    /**
     * 1.21.6 - 1.21.8
     */
    R24("1.21.6"),
    /**
     * 1.21.5
     */
    R23("1.21.5"),
    /**
     * 1.21.4
     */
    R22("1.21.4"),
    /**
     * 1.21.2 - 1.21.3
     */
    R21("1.21.2"),
    /**
     * 1.21 - 1.21.1
     */
    R20("1.21"),
    /**
     * 1.20.5 - 1.20.6
     */
    R19("1.20.5"),
    /**
     * 1.20.3 - 1.20.4
     */
    R18("1.20.3"),
    /**
     * 1.20.2
     */
    R17("1.20.2"),
    /**
     * 1.20 - 1.20.1
     */
    R16("1.20"),
    /**
     * 1.19.4 - 1.19.5
     */
    R15("1.19.4"),
    /**
     * 1.19.3
     */
    R14("1.19.3"),
    /**
     * 1.19.1 - 1.19.2
     */
    R13("1.19.1"),
    /**
     * 1.19
     */
    R12("1.19"),
    /**
     * 1.18.2
     */
    R11("1.18.2"),
    /**
     * 1.18 - 1.18.1
     */
    R10("1.18"),
    /**
     * 1.17.1
     */
    R9("1.17.1"),
    /**
     * 1.17
     */
    R8("1.17"),
    /**
     * 1.16.4 - 1.16.5
     */
    R7("1.16.4"),
    /**
     * 1.16.2 - 1.16.3
     */
    R6("1.16.2"),
    /**
     * 1.16 - 1.16.1
     */
    R5("1.16"),
    /**
     * 1.15 - 1.15.2
     */
    R4("1.15"),
    /**
     * 1.14.4
     */
    R3("1.14.4"),
    /**
     * 1.14.1 - 1.14.3
     */
    R2("1.14.1"),
    /**
     * 1.14
     */
    R1("1.14");

    companion object {
        /**
         * Returns the [MinecraftRevisions] enum constant that matches the given version string.
         *
         * @param version The version string to match.
         * @return The matching [MinecraftRevisions] enum constant, or null if no match is found.
         */
        fun fromVersion(version: String): MinecraftRevisions {
            return entries.find { it.version >= version }!!
        }

        /**
         * Returns the [MinecraftRevisions] enum constant that matches the given [SemVersion].
         *
         * @param version The [SemVersion] to match.
         *
         * @return The matching [MinecraftRevisions] enum constant, or null if no match is found.
         */
        fun fromVersion(version: SemVersion): MinecraftRevisions {
            return entries.find { it.version >= version.toString() }!!
        }

        /**
         * Returns the [MinecraftRevisions] enum constant that matches the given [SemVersion].
         *
         * @param version The [SemVersion] to match.
         * @return The matching [MinecraftRevisions] enum constant
         */
        val latest: MinecraftRevisions
            get() = entries.first()
    }
}