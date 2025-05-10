package org.bxteam.runserver.util.lib

import com.google.gson.JsonParser
import org.gradle.api.logging.Logging
import java.net.URI

object VersionLib {
    private const val MCJARS_API_BASE = "https://mcjars.app/api/v2"
    private val logger = Logging.getLogger(VersionLib::class.java)

    /**
     * Used to get a list of all supported for `Spigot`.
     *
     * @return A list of all supported versions for `Spigot`.
     */
    fun spigot(): List<String> = getMcJarsVersions("SPIGOT")

    /**
     * Used to get a list of all supported for `PaperMC`.
     *
     * @return A list of all supported versions for `PaperMC`.
     */
    fun paper(): List<String> = getMcJarsVersions("PAPER")

    /**
     * Used to get a list of all supported for `Pufferfish`.
     *
     * @return A list of all supported versions for `Pufferfish`.
     */
    fun pufferfish(): List<String> = getMcJarsVersions("PUFFERFISH")

    /**
     * Used to get a list of all supported for `Purpur`.
     *
     * @return A list of all supported versions for `Purpur`.
     */
    fun purpur(): List<String> = getMcJarsVersions("PURPUR")

    /**
     * Used to get a list of all supported for `Canvas`.
     *
     * @return A list of all supported versions for `Canvas`.
     */
    fun canvas(): List<String> = getMcJarsVersions("CANVAS")

    /**
     * Used to get a list of all supported for `DivineMC`.
     *
     * @return A list of all supported versions for `DivineMC`.
     */
    fun divinemc(): List<String> = getMcJarsVersions("DIVINEMC")

    /**
     * Used to get a list of all supported for `Leaf`.
     *
     * @return A list of all supported versions for `Leaf`.
     */
    fun leaf(): List<String> = getMcJarsVersions("LEAF")

    /**
     * Used to get a list of all supported for `Leaves`.
     *
     * @return A list of all supported versions for `Leaves`.
     */
    fun leaves(): List<String> = getMcJarsVersions("LEAVES")

    /**
     * Used to get a list of all supported for `Bungeecord`.
     *
     * @return A list of versions for `Bungeecord`.
     */
    fun bungeecord(): List<String> = getMcJarsVersions("BUNGEECORD")

    /**
     * Used to get a list of all supported for `Velocity`.
     *
     * @return A list of all supported versions for `Velocity`.
     */
    fun velocity(): List<String> = getMcJarsVersions("VELOCITY")

    /**
     * Used to get a list of all supported for `Waterfall`.
     *
     * @return A list of all supported versions for `Waterfall`.
     */
    fun waterfall(): List<String> = getMcJarsVersions("WATERFALL")

    /**
     * Gets versions available for a specific server type from the McJars API.
     *
     * @param type The server type to get versions for
     * @return A list of available versions
     */
    private fun getMcJarsVersions(type: String): List<String> {
        logger.lifecycle("Fetching available versions for $type from McJars API...")
        try {
            val url = URI("$MCJARS_API_BASE/builds/$type")
            logger.debug("Requesting API endpoint: $url")
            
            val response = JsonParser.parseString(url.toURL().readText()).asJsonObject
            
            if (!response.get("success").asBoolean) {
                logger.warn("API request was not successful for $type")
                return emptyList()
            }

            val builds = response.getAsJsonObject("builds")
            val versions = builds.keySet().toList()
            
            logger.lifecycle("Found ${versions.size} available version(s) for $type")
            logger.debug("Available versions: $versions")
            
            return versions
        } catch (e: Exception) {
            logger.error("Failed to fetch versions for $type: ${e.message}")
            return emptyList()
        }
    }
}
