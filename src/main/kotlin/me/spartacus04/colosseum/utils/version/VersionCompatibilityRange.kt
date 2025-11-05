package me.spartacus04.colosseum.utils.version

/**
 * Annotation to mark classes that are compatible with specific Minecraft server versions.
 *
 * @property since The starting version from which the class is compatible.
 * @property until The ending version until which the class is compatible.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class VersionCompatibilityRange(
    val since: String,
    val until: String,
)