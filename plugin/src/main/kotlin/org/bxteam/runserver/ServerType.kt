package org.bxteam.runserver

import org.bxteam.runserver.lib.DownloadLib
import org.bxteam.runserver.lib.VersionLib
import java.io.File

enum class ServerType(val loaderName: String, val proxy: Boolean) {
    SPIGOT("spigot", false),
    PAPER("paper", false),
    PUFFERFISH("paper", false),
    PURPUR("purpur", false),
    CANVAS("canvas", false),
    DIVINEMC("divinemc", false),
    LEAF("leaf", false),
    LEAVES("leaves", false),
    BUNGEECORD("bungeecord", true),
    VELOCITY("velocity", true),
    VELOCITY_CTD("velocity-ctd", true),
    WATERFALL("waterfall", true);

    /**
     * Used to download a Jar from a `ServerType`.
     *
     * @param serverVersion The server version.
     * @param directory The directory in which it will be saved in.
     */
    fun downloadJar(serverVersion: String, directory: File) =
        when(this) {
            SPIGOT -> DownloadLib.spigot(directory, serverVersion)
            PAPER -> DownloadLib.paper(directory, serverVersion)
            PUFFERFISH -> DownloadLib.pufferfish(directory, serverVersion)
            PURPUR -> DownloadLib.purpur(directory, serverVersion)
            CANVAS -> DownloadLib.canvas(directory, serverVersion)
            DIVINEMC -> DownloadLib.divinemc(directory, serverVersion)
            LEAF -> DownloadLib.leaf(directory, serverVersion)
            LEAVES -> DownloadLib.leaves(directory, serverVersion)
            BUNGEECORD -> DownloadLib.bungeecord(directory, serverVersion)
            VELOCITY -> DownloadLib.velocity(directory, serverVersion)
            VELOCITY_CTD -> DownloadLib.velocityCtd(directory, serverVersion)
            WATERFALL -> DownloadLib.waterfall(directory, serverVersion)
        }

    /**
     * Used to get a list of all supported Minecraft server versions for this `ServerType`.
     */
    fun versions(): List<String> =
        when(this) {
            SPIGOT -> VersionLib.spigot()
            PAPER -> VersionLib.paper()
            PUFFERFISH -> VersionLib.pufferfish()
            PURPUR -> VersionLib.purpur()
            CANVAS -> VersionLib.canvas()
            DIVINEMC -> VersionLib.divinemc()
            LEAF -> VersionLib.leaf()
            LEAVES -> VersionLib.leaves()
            BUNGEECORD -> VersionLib.bungeecord()
            VELOCITY -> VersionLib.velocity()
            VELOCITY_CTD -> VersionLib.velocityCtd()
            WATERFALL -> VersionLib.waterfall()
        }
}