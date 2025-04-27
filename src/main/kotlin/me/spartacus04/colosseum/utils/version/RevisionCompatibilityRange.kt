package me.spartacus04.colosseum.utils.version


/**
 * Annotation to mark classes that are compatible with specific net.minecraft.server revisions.
 *
 * @property since The starting revision from which the class is compatible.
 * @property until The ending revision until which the class is compatible.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RevisionCompatibilityRange(
    val since: MinecraftRevisions,
    val until: MinecraftRevisions,
)