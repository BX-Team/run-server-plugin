package org.bxteam.runserver.exception

/**
 * This exception will be throw then the system doesn't have the correct version of java installed.
 */
class UnsupportedJavaVersionException(minVersion: Int, maxVersion: Int) : Exception("You aren't using the correct Java version. Please use any version from class file major version $minVersion to $maxVersion.")
