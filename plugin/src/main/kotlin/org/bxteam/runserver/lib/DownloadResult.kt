package org.bxteam.runserver.lib

import java.io.File

/**
 * A data class that is returned when downloading a server jar.
 *
 * @param resultType If the download was a success or not
 * @param errorMessage If the download failed it will give the error message
 * @param jarFile If the download was a success then this will be the jar file
 * @param startTime The time when the download started
 */
data class DownloadResult(
    val resultType: DownloadResultType,
    val errorMessage: String?,
    val jarFile: File?,
    val startTime: Long = System.currentTimeMillis()
)

/**
 * Enum class for the download result
 *
 * @since 1.0.0
 */
enum class DownloadResultType {
    /**
     * That the download was a success
     */
    SUCCESS,

    /**
     * That the download failed
     */
    FAILED
}
