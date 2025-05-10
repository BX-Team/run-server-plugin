package org.bxteam.runserver.lib

import com.google.gson.JsonParser
import org.gradle.api.logging.Logging
import java.io.File
import java.io.FileOutputStream
import java.net.URI

object DownloadLib {
    private const val MCJARS_API_BASE = "https://mcjars.app/api/v2"
    private val logger = Logging.getLogger(DownloadLib::class.java)

    /**
     * This method is used to download Spigot.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun spigot(folder: File, minecraftVersion: String) =
        downloadFromMcJarsApi(folder, "SPIGOT", minecraftVersion)

    /**
     * This method is used to download PaperMC.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun paper(folder: File, minecraftVersion: String) =
        downloadFromMcJarsApi(folder, "PAPER", minecraftVersion)

    /**
     * This method is used to download Pufferfish.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun pufferfish(folder: File, minecraftVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "PUFFERFISH", minecraftVersion)

    /**
     * This method is used to download Purpur.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun purpur(folder: File, minecraftVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "PURPUR", minecraftVersion)

    /**
     * This method is used to download Canvas.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun canvas(folder: File, minecraftVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "CANVAS", minecraftVersion)

    /**
     * This method is used to download DivineMC.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun divinemc(folder: File, minecraftVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "DIVINEMC", minecraftVersion)

    /**
     * This method is used to download Leaf.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun leaf(folder: File, minecraftVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "LEAF", minecraftVersion)

    /**
     * This method is used to download Leaves.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun leaves(folder: File, minecraftVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "LEAVES", minecraftVersion)

    /**
     * This method is used to download Bungeecord proxy.
     *
     * @param folder The folder to download the jar to
     */
    fun bungeecord(folder: File): DownloadResult =
        downloadFromMcJarsApi(folder, "BUNGEECORD", "latest")

    /**
     * This method is used to download Velocity proxy.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun velocity(folder: File, minecraftVersion: String) =
        downloadFromMcJarsApi(folder, "VELOCITY", minecraftVersion)

    /**
     * This method is used to download Waterfall proxy.
     *
     * @param folder The folder to download the jar to
     * @param minecraftVersion The minecraft version target
     */
    fun waterfall(folder: File, minecraftVersion: String) =
        downloadFromMcJarsApi(folder, "WATERFALL", minecraftVersion)

    /**
     * This method is used to download file from the McJars API.
     */
    private fun downloadFromMcJarsApi(folder: File, type: String, minecraftVersion: String): DownloadResult {
        logger.lifecycle("Fetching ${type.lowercase()} builds for version $minecraftVersion...")
        try {
            val url = URI("$MCJARS_API_BASE/builds/$type/$minecraftVersion")
            val response = JsonParser.parseString(url.toURL().readText()).asJsonObject

            if (!response.get("success").asBoolean) {
                return DownloadResult(DownloadResultType.FAILED, "API request failed", null)
            }

            val builds = response.getAsJsonArray("builds")
            if (builds.isEmpty) {
                return DownloadResult(DownloadResultType.FAILED, "No builds available", null)
            }

            val latestBuild = builds.get(0).asJsonObject
            val buildNumber = latestBuild.get("name").asString
            val jarUrl = latestBuild.get("jarUrl").asString
            val outputFileName = type.lowercase() + ".jar"
            
            logger.lifecycle("Latest build for $minecraftVersion is $buildNumber.")
            logger.lifecycle("Downloading $type $minecraftVersion build $buildNumber...")

            val result = downloadFile(folder, jarUrl, outputFileName)
            if (result.resultType == DownloadResultType.SUCCESS) {
                logger.lifecycle("Done downloading $type, took ${formatTime(System.currentTimeMillis() - result.startTime)}.")
            }
            return result
        } catch (exception: Exception) {
            return DownloadResult(DownloadResultType.FAILED, exception.message, null)
        }
    }

    /**
     * This method is used to download a file from an url.
     */
    fun downloadFile(folder: File, downloadURL: String, name: String): DownloadResult =
        downloadFile(folder, URI(downloadURL), name)

    /**
     * This method is used to download a file from an url.
     */
    private fun downloadFile(folder: File, downloadURL: URI, name: String): DownloadResult {
        val file = File(folder, name)
        val startTime = System.currentTimeMillis()

        return if (file.exists()) {
            DownloadResult(DownloadResultType.SUCCESS, null, file, startTime)
        } else {
            try {
                downloadURL.toURL().openConnection().let { connection ->
                    connection.getInputStream().use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                
                DownloadResult(DownloadResultType.SUCCESS, null, file, startTime)
            } catch (exception: Exception) {
                DownloadResult(DownloadResultType.FAILED, exception.message, null, startTime)
            }
        }
    }
    
    /**
     * Format time in milliseconds to human-readable format
     */
    private fun formatTime(timeInMs: Long): String {
        if (timeInMs < 1000) return "$timeInMs ms"
        return String.format("%.2fs", timeInMs / 1000.0)
    }
}
