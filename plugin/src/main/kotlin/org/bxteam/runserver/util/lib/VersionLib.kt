package org.bxteam.runserver.util.lib

import com.google.gson.JsonParser
import java.net.URI

object VersionLib {
    private const val MCJARS_API_BASE = "https://api.mcjars.app/api/v2"

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
        try {
            val url = URI("$MCJARS_API_BASE/versions/$type")
            val response = JsonParser.parseString(url.toURL().readText()).asJsonObject

            if (!response.get("success").asBoolean) {
                return emptyList()
            }

            val versions = response.getAsJsonArray("versions")
            return versions.map { it.asString }
        } catch (e: Exception) {
            return emptyList()
        }
    }
}
