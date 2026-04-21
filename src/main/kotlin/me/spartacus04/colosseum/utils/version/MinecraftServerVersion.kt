package me.spartacus04.colosseum.utils.version

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

/**
 * Represents the version of the Minecraft server.
 *
 * @property plugin The plugin instance.
 */
@Suppress("unused")
class MinecraftServerVersion(plugin: Plugin) : SemVersion(plugin.server.bukkitVersion.split("-")[0]) {
    /**
     * The server branding.
     */
    val branding: String = plugin.server.name

    /**
     * Whether the server is running Paper.
     */
    val isPaper: Boolean
        get() = branding.contains("Paper")

    /**
     * Whether the server is running Spigot.
     */
    val isSpigot: Boolean
        get() = branding.contains("Spigot")

    /**
     * Whether the server is running Folia.
     */
    val isFolia: Boolean
        get() = branding.contains("Folia")

    /**
     * Whether the server is running a legacy version (1.21.1 or older).
     */
    val isLegacy: Boolean
        get() = compareTo("1.21.1") < 0

    /**
     * Whether the server is running a modern version (26.1 or newer).
     */
    val isModern: Boolean
        get() = compareTo("21.1") >= 0


    fun isVersionAnnotationCompatible(clazz: Class<*>) : Boolean {
        val annotation = clazz.getAnnotation(VersionCompatibilityRange::class.java)

        if (annotation != null) {
            return this >= annotation.since && this <= annotation.until
        }

        val annotationMin = clazz.getAnnotation(VersionCompatibilityMin::class.java)

        if (annotationMin != null) {
            return this >= annotationMin.since
        }

        return true
    }

    companion object {
        /**
         * The current Minecraft version.
         */
        val current: MinecraftServerVersion
            get() = MinecraftServerVersion(Bukkit.getPluginManager().plugins.first())
    }
}