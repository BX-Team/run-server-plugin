package org.bxteam.runserver.exception

/**
 * Exception thrown specified Minecraft version is not found.
 */
class VersionNotFoundException(mcVersion: String, versions: List<String>): Exception("$mcVersion wasn't found inside $versions")
