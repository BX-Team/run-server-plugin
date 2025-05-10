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
    WATERFALL("waterfall", true);

    /**
     * Used to download a Jar from a `ServerType`.
     *
     * @param mcVersion The Minecraft server version.
     * @param directory The directory in which it will be saved in.
     */
    fun downloadJar(mcVersion: String, directory: File) =
        when(this) {
            SPIGOT -> DownloadLib.spigot(directory, mcVersion)
            PAPER -> DownloadLib.paper(directory, mcVersion)
            PUFFERFISH -> DownloadLib.pufferfish(directory, mcVersion)
            PURPUR -> DownloadLib.purpur(directory, mcVersion)
            CANVAS -> DownloadLib.canvas(directory, mcVersion)
            DIVINEMC -> DownloadLib.divinemc(directory, mcVersion)
            LEAF -> DownloadLib.leaf(directory, mcVersion)
            LEAVES -> DownloadLib.leaves(directory, mcVersion)
            BUNGEECORD -> DownloadLib.bungeecord(directory)
            VELOCITY -> DownloadLib.velocity(directory, mcVersion)
            WATERFALL -> DownloadLib.waterfall(directory, mcVersion)
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
            WATERFALL -> VersionLib.waterfall()
        }
}