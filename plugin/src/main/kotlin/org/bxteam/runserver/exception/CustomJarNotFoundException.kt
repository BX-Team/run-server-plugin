package org.bxteam.runserver.exception

/**
 * Exception thrown when a custom jar file is not found in `customJarPath` and `run` directories.
 */
class CustomJarNotFoundException : Exception("Your custom jar was not found inside the customJarPath and run directories.")
