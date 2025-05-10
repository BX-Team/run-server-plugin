package org.bxteam.runserver.lib

import com.google.gson.JsonParser
import org.bxteam.runserver.ServerType
import java.io.File
import java.net.URI

/**
 * Configuration class for plugin downloads
 */
class PluginDownloadLib {
    private val downloads = mutableListOf<PluginDownload>()

    companion object {
        /**
         * List of server types that are compatible with Paper plugins
         */
        internal val PAPER_COMPATIBLE_TYPES = setOf(
            ServerType.PAPER,
            ServerType.PUFFERFISH,
            ServerType.PURPUR,
            ServerType.CANVAS,
            ServerType.DIVINEMC,
            ServerType.LEAF,
            ServerType.LEAVES
        )

        /**
         * Check if a server type is compatible with a loader
         */
        internal fun isCompatible(serverType: ServerType, loader: String): Boolean {
            return when {
                loader.equals(serverType.loaderName, ignoreCase = true) -> true
                loader.equals("paper", ignoreCase = true) && serverType in PAPER_COMPATIBLE_TYPES -> true
                else -> false
            }
        }
    }

    /**
     * Add a Modrinth plugin download
     * @param projectId The Modrinth project ID
     * @param version The specific version to download
     */
    fun modrinth(projectId: String, version: String) {
        downloads.add(ModrinthDownload(projectId, version))
    }

    /**
     * Add a GitHub plugin download
     * @param owner The repository owner
     * @param repo The repository name
     * @param tag The release tag
     * @param fileName The specific file name to download
     */
    fun github(owner: String, repo: String, tag: String, fileName: String) {
        downloads.add(GithubDownload(owner, repo, tag, fileName))
    }

    /**
     * Add a Hangar plugin download
     * @param projectId The Hangar project ID
     * @param version The specific version to download
     */
    fun hangar(projectId: String, version: String) {
        downloads.add(HangarDownload(projectId, version))
    }

    /**
     * Add a direct URL plugin download
     * @param url The direct download URL
     */
    fun url(url: String) {
        downloads.add(UrlDownload(url))
    }

    /**
     * Execute all plugin downloads
     * @param folder The target folder to download plugins to
     * @param serverType The server type for compatibility checks
     * @return List of download results
     */
    fun execute(folder: File, serverType: ServerType): List<DownloadResult> {
        return downloads.map { it.download(folder, serverType) }
    }
}

/**
 * Base interface for plugin downloads
 */
sealed interface PluginDownload {
    fun download(folder: File, serverType: ServerType): DownloadResult
}

/**
 * Modrinth plugin download
 */
data class ModrinthDownload(
    val projectId: String,
    val version: String
) : PluginDownload {
    override fun download(folder: File, serverType: ServerType): DownloadResult {
        try {
            val apiUrl = "https://api.modrinth.com/v2/project/$projectId/version/$version"
            val response = JsonParser.parseString(URI(apiUrl).toURL().readText()).asJsonObject

            val loaders = response.getAsJsonArray("loaders")
            if (!loaders.any { PluginDownloadLib.isCompatible(serverType, it.asString) }) {
                return DownloadResult(
                    DownloadResultType.FAILED,
                    "Plugin version is not compatible with ${serverType.name} server",
                    null
                )
            }

            val files = response.getAsJsonArray("files")
            val primaryFile = files.firstOrNull { it.asJsonObject.get("primary").asBoolean }
                ?: return DownloadResult(DownloadResultType.FAILED, "No primary file found", null)

            val downloadUrl = primaryFile.asJsonObject.get("url").asString
            val fileName = primaryFile.asJsonObject.get("filename").asString

            println("Downloading modrinth:$projectId:$version...")
            val result = DownloadLib.downloadFile(folder, downloadUrl, fileName)
            if (result.resultType == DownloadResultType.SUCCESS) {
                println("Done downloading modrinth:$projectId:$version, took ${formatTime(System.currentTimeMillis() - result.startTime)}.")
            }
            return result
        } catch (e: Exception) {
            return DownloadResult(DownloadResultType.FAILED, "Failed to download from Modrinth: ${e.message}", null)
        }
    }
}

/**
 * GitHub plugin download
 */
data class GithubDownload(
    val owner: String,
    val repo: String,
    val tag: String,
    val fileName: String
) : PluginDownload {
    override fun download(folder: File, serverType: ServerType): DownloadResult {
        val url = "https://github.com/$owner/$repo/releases/download/$tag/$fileName"
        println("Downloading github:$owner/$repo:$tag...")
        val result = DownloadLib.downloadFile(folder, url, fileName)
        if (result.resultType == DownloadResultType.SUCCESS) {
            println("Done downloading github:$owner/$repo:$tag, took ${formatTime(System.currentTimeMillis() - result.startTime)}.")
        }
        return result
    }
}

/**
 * Hangar plugin download
 */
data class HangarDownload(
    val projectId: String,
    val version: String
) : PluginDownload {
    override fun download(folder: File, serverType: ServerType): DownloadResult {
        try {
            val apiUrl = "https://hangar.papermc.io/api/v1/projects/$projectId/versions/$version"
            val response = JsonParser.parseString(URI(apiUrl).toURL().readText()).asJsonObject

            val downloads = response.getAsJsonObject("downloads")
            val serverDownloads = downloads.getAsJsonObject(serverType.name.uppercase())
                ?: if (serverType in PluginDownloadLib.PAPER_COMPATIBLE_TYPES) {
                    downloads.getAsJsonObject("PAPER")
                } else {
                    null
                }
                ?: return DownloadResult(
                    DownloadResultType.FAILED,
                    "No download available for ${serverType.name} server",
                    null
                )

            val fileInfo = serverDownloads.getAsJsonObject("fileInfo")
            val downloadUrl = serverDownloads.get("downloadUrl").asString
            val fileName = fileInfo.get("name").asString

            println("Downloading hangar:$projectId:$version:${serverType.name.uppercase()}...")
            val result = DownloadLib.downloadFile(folder, downloadUrl, fileName)
            if (result.resultType == DownloadResultType.SUCCESS) {
                println("Done downloading hangar:$projectId:$version:${serverType.name.uppercase()}, took ${formatTime(System.currentTimeMillis() - result.startTime)}.")
            }
            return result
        } catch (e: Exception) {
            return DownloadResult(DownloadResultType.FAILED, "Failed to download from Hangar: ${e.message}", null)
        }
    }
}

/**
 * Direct URL plugin download
 */
data class UrlDownload(
    val url: String
) : PluginDownload {
    override fun download(folder: File, serverType: ServerType): DownloadResult {
        val fileName = url.substringAfterLast("/")
        println("Downloading $fileName...")
        val result = DownloadLib.downloadFile(folder, url, fileName)
        if (result.resultType == DownloadResultType.SUCCESS) {
            println("Done downloading $fileName, took ${formatTime(System.currentTimeMillis() - result.startTime)}.")
        }
        return result
    }
}

/**
 * Format time in milliseconds to human-readable format
 */
private fun formatTime(timeInMs: Long): String {
    if (timeInMs < 1000) return "$timeInMs ms"
    return String.format("%.2fs", timeInMs / 1000.0)
}
