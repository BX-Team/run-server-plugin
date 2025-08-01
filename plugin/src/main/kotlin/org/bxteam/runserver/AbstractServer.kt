package org.bxteam.runserver

import com.google.gson.JsonParser
import org.bxteam.runserver.exception.UnsupportedJavaVersionException
import org.bxteam.runserver.lib.TaskLib
import org.gradle.api.JavaVersion
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import java.io.File
import java.net.URI

abstract class AbstractServer : JavaExec() {
    @get:Internal
    protected var runDir: File? = null
    @get:Internal
    protected var pluginDir: File? = null

    @get:Internal
    protected var serverType: ServerType = ServerType.SPIGOT
    @get:Internal
    protected var serverVersion: String? = null

    @get:Internal
    protected var versionFolder: Boolean = false

    @get:Internal
    protected val projectDirectory: DirectoryProperty = project.objects.directoryProperty()

    @get:Internal
    protected val pluginJarFile: RegularFileProperty = project.objects.fileProperty()

    init {
        projectDirectory.set(project.layout.projectDirectory)
    }

    /**
     * This is an option to change if there should be a folder for every version
     *
     * @param boolean If a folder should be created per version
     */
    fun perVersionFolder(boolean: Boolean) { versionFolder = boolean }

    /**
     * The minecraft version the server should run
     *
     * @param serverVersion The minecraft version
     */
    fun serverVersion(serverVersion: String) {
        this.serverVersion = serverVersion
        dependOnTasks()
        if (!serverType.proxy) checkJavaVersion(serverVersion, javaVersion)
    }

    /**
     * This option allowed you to set the folder where the server is running
     *
     * @param folder This gives you the folder data and returns the file to place the server folder
     */
    fun serverFolder(folder: (FolderData).() -> File) { runDir = folder(FolderData(serverVersion, serverType, projectDirectory.asFile.get())) }

    /**
     * This option allowed you to set the folder where the server is running
     *
     * @param folder This gives you the folder data and returns the file to place the server folder
     */
    fun serverFolderName(folder: (FolderData).() -> String) { runDir = File(projectDirectory.asFile.get(), folder(
        FolderData(serverVersion, serverType, projectDirectory.asFile.get())
    )) }

    /**
     * This option allowed you to set the folder where the server is running
     *
     * @param name The server folder name
     */
    fun serverFolderName(name: String) { runDir = File(projectDirectory.asFile.get(), name) }

    /**
     * This option allows you to set what type of server you will be running.
     *
     * @param serverType The server type
     */
    fun serverType(serverType: ServerType) {
        this.serverType = serverType
    }

    protected fun setRunningDir(file: File) = file.also { runDir = it }
    protected fun setClass(file: File): JavaExec = classpath(file.path)
    override fun setJvmArgs(args: List<String>) {
        jvmArgs(args)
    }

    /**
     * This sets up the `runDir` of JavaExec
     */
    protected fun setup() {
        standardInput = System.`in`
        systemProperty("file.encoding", "UTF-8")
        if (runDir == null) {
            runDir = File(projectDirectory.asFile.get(), "run${if (versionFolder) "/$serverVersion" else ""}/${serverType.name.lowercase()}")
        }
        pluginDir = File(runDir, "plugins")
        workingDir(runDir!!.path)
    }

    /**
     * Checks and depends on the correct tasks
     */
    private fun dependOnTasks() {
        val taskProvider = when {
            TaskLib.TaskNames.REMAP in project.tasks.names -> project.tasks.named(TaskLib.TaskNames.REMAP)
            TaskLib.TaskNames.SHADOW in project.tasks.names -> project.tasks.named(TaskLib.TaskNames.SHADOW)
            else -> project.tasks.named(TaskLib.TaskNames.JAR)
        }

        pluginJarFile.set(taskProvider.flatMap { project.layout.file(project.provider { it.outputs.files.singleFile }) })
        setDependsOn(mutableListOf(taskProvider))
    }

    /**
     * Throws a [UnsupportedJavaVersionException] if the correct Java version is not used.
     */
    private fun checkJavaVersion(serverVersion: String, javaVersion: JavaVersion) {
        val uri = URI.create("https://hub.spigotmc.org/versions/$serverVersion.json").toURL()
        val response = JsonParser.parseString(uri.readText()).asJsonObject

        val versionsArray = response["javaVersions"].asJsonArray
        val minJava = versionsArray[0].asInt
        val maxJava = versionsArray[1].asInt

        val classFileMajorVersion: Int = javaVersion.majorVersion.toInt() + 44
        if (classFileMajorVersion in minJava..maxJava) return

        throw UnsupportedJavaVersionException(minJava, maxJava)
    }

}

/**
 * The folder data that is giving when selecting the folder to make the server go to.
 *
 * @param serverVersion The version the server is running
 * @param serverType The server type that its running
 * @param buildFolder The build folder of your project
 * @since 1.0.0
 */
class FolderData(private val serverVersion: String?, private val serverType: ServerType, private val buildFolder: File)
