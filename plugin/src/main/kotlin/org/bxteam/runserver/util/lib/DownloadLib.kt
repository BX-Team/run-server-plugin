package org.bxteam.runserver.util.lib

import com.google.gson.JsonParser
import java.io.File
import java.io.FileOutputStream
import java.net.URI

object DownloadLib {
    private const val MCJARS_API_BASE = "https://api.mcjars.app/api/v2"

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
     *
     * @param folder The folder to download the jar to
     * @param type The type of server to download
     * @param minecraftVersion The minecraft version target
     * @return The result of the download operation
     */
    private fun downloadFromMcJarsApi(folder: File, type: String, minecraftVersion: String): DownloadResult {
        try {
            val url = URI("$MCJARS_API_BASE/builds/$type/$minecraftVersion")
            val response = JsonParser.parseString(url.toURL().readText()).asJsonObject

            if (!response.get("success").asBoolean) {
                return DownloadResult(DownloadResultType.FAILED, "API request failed", null)
            }

            val builds = response.getAsJsonArray("builds")
            if (builds.isEmpty) {
                return DownloadResult(DownloadResultType.FAILED, "No builds available for $type $minecraftVersion", null)
            }

            // Get the first (latest) build
            val latestBuild = builds.get(0).asJsonObject
            val jarUrl = latestBuild.get("jarUrl").asString
            val outputFileName = type.lowercase() + ".jar"

            return downloadFile(folder, jarUrl, outputFileName)
        } catch (exception: Exception) {
            return DownloadResult(DownloadResultType.FAILED, exception.message, null)
        }
    }

    /**
     * This method is used to download a file from an url.
     *
     * @param folder The folder to download the jar to
     * @param downloadURL The url to download from
     * @param name The name to give the downloaded file
     */
    fun downloadFile(folder: File, downloadURL: String, name: String): DownloadResult =
        downloadFile(folder, URI(downloadURL), name)

    /**
     * This method is used to download a file from an url.
     *
     * @param folder The folder to download the jar to
     * @param downloadURL The url to download from
     * @param name The name to give the downloaded file
     *
     * @return This method will return the download result if it fails or not
     */
    private fun downloadFile(folder: File, downloadURL: URI, name: String): DownloadResult {
        val file = File(folder, name)

        return if (file.exists()) {
            DownloadResult(DownloadResultType.SUCCESS, null, file)
        } else {
            try {
                downloadURL.toURL().openStream().use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                DownloadResult(DownloadResultType.SUCCESS, null, file)
            } catch (exception: Exception) {
                DownloadResult(DownloadResultType.FAILED, exception.message, null)
            }
        }
    }
}
