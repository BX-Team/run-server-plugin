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
     * @param serverVersion The minecraft version target
     */
    fun spigot(folder: File, serverVersion: String) =
        downloadFromMcJarsApi(folder, "SPIGOT", serverVersion)

    /**
     * This method is used to download PaperMC.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun paper(folder: File, serverVersion: String) =
        downloadFromMcJarsApi(folder, "PAPER", serverVersion)

    /**
     * This method is used to download Pufferfish.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun pufferfish(folder: File, serverVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "PUFFERFISH", serverVersion)

    /**
     * This method is used to download Purpur.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun purpur(folder: File, serverVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "PURPUR", serverVersion)

    /**
     * This method is used to download Canvas.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun canvas(folder: File, serverVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "CANVAS", serverVersion)

    /**
     * This method is used to download DivineMC.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun divinemc(folder: File, serverVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "DIVINEMC", serverVersion)

    /**
     * This method is used to download Leaf.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun leaf(folder: File, serverVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "LEAF", serverVersion)

    /**
     * This method is used to download Leaves.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun leaves(folder: File, serverVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "LEAVES", serverVersion)

    /**
     * This method is used to download Bungeecord proxy.
     *
     * @param folder The folder to download the jar to
     */
    fun bungeecord(folder: File, serverVersion: String): DownloadResult =
        downloadFromMcJarsApi(folder, "BUNGEECORD", serverVersion)

    /**
     * This method is used to download Velocity proxy.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun velocity(folder: File, serverVersion: String) =
        downloadFromMcJarsApi(folder, "VELOCITY", serverVersion)

    /**
     * This method is used to download Velocity-CTD proxy.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun velocityCtd(folder: File, serverVersion: String) =
        downloadFromMcJarsApi(folder, "VELOCITY_CTD", serverVersion)

    /**
     * This method is used to download Waterfall proxy.
     *
     * @param folder The folder to download the jar to
     * @param serverVersion The minecraft version target
     */
    fun waterfall(folder: File, serverVersion: String) =
        downloadFromMcJarsApi(folder, "WATERFALL", serverVersion)

    /**
     * This method is used to download file from the McJars API.
     */
    private fun downloadFromMcJarsApi(folder: File, type: String, serverVersion: String): DownloadResult {
        logger.lifecycle("Fetching ${type.lowercase()} builds for version $serverVersion...")
        try {
            val url = URI("$MCJARS_API_BASE/builds/$type/$serverVersion")
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
            
            logger.lifecycle("Latest build for $serverVersion is $buildNumber.")
            logger.lifecycle("Downloading $type $serverVersion build $buildNumber...")

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
     * This method is used to download a file from a url.
     */
    fun downloadFile(folder: File, downloadURL: String, name: String): DownloadResult =
        downloadFile(folder, URI(downloadURL), name)

    /**
     * This method is used to download a file from a url.
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
