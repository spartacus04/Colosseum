package me.spartacus04.colosseum.config

/**
 * Represents a field in a configuration file.
 *
 * @property name The name of the field.
 * @property description The description of the field.
 * @property defaultValue The default value of the field.
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigField(
    val name: String,
    val description: String,
    val defaultValue: String,
)