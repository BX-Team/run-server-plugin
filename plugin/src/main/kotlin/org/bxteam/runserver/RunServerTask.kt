package org.bxteam.runserver

import org.bxteam.runserver.exception.VersionNotFoundException
import org.bxteam.runserver.util.RamAmount
import org.bxteam.runserver.lib.DownloadResult
import org.bxteam.runserver.lib.DownloadResultType
import org.bxteam.runserver.lib.PluginLib
import org.bxteam.runserver.lib.TaskLib
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

abstract class RunServerTask : AbstractServer() {
    private var allowedRam: String = "2G"
    private var noGui: Boolean = true
    private var downloads: MutableList<Pair<String, Boolean>> = mutableListOf()
    private var filePlugins: MutableList<Pair<File, Boolean>> = mutableListOf()
    private var acceptEula: Boolean = false
    private var customJarName: String? = null
    private var inputTask: TaskProvider<*>? = null
    private var debug: Boolean = false

    init {
        outputs.upToDateWhen { false }
    }

    /**
     * This is an option to select the input task
     *
     * @param task The new input task
     */
    fun inputTask(task: TaskProvider<*>) {
        inputTask = task
        setDependsOn(mutableListOf(task))
    }

    /**
     * This is an option to set the amount of ram the server is allowed to use.
     *
     * @param amount This amount
     * @param ramAmount What type of ram
     */
    fun allowedRam(amount: Int, ramAmount: RamAmount) { allowedRam = "$amount${ramAmount.flag}" }

    /**
     * This is an option is allowing some server jar to create a gui on start
     *
     * No gui will not have any effect on versions before 1.15.2
     *
     * @param boolean allow or disallow
     */
    fun noGui(boolean: Boolean) { noGui = boolean }

    /**
     * This option will throw out exceptions
     */
    fun debugMessage(debug: Boolean) { this.debug = debug }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param links The page links to download from.
     */
    fun plugins(vararg links: String) { downloads.addAll(links.map { Pair(it, false) }) }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param links The page links to download from and able to set if it will overwrite the old file
     */
    fun plugins(links: List<Pair<String, Boolean>>) { downloads.addAll(links) }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param links The page links to download from
     * @param overwrite If it should overwrite every time
     */
    fun plugins(links: List<String>, overwrite: Boolean) { downloads.addAll(links.map { Pair(it, overwrite) }) }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param url The url to the page to down from
     * @param overwrite If it should overwrite the file everytime
     */
    fun plugin(url: String, overwrite: Boolean = false) { downloads.add(Pair(url, overwrite)) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param files A list of plugins to copy
     */
    fun filePlugins(vararg files: File) { filePlugins.addAll(files.map { Pair(it, false) }) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param files A list of plugins to copy
     * @param overwrite If it should overwrite the files
     */
    fun filePlugins(files: List<File>, overwrite: Boolean) { filePlugins.addAll(files.map { Pair(it, overwrite) }) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param files A list of plugins to copy and if it will overwrite the plugin file
     */
    fun filePlugins(files: List<Pair<File, Boolean>>) { filePlugins.addAll(files) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param file The plugin to copy
     * @param overwrite If it should overwrite the plugin file
     */
    fun filePlugin(file: File, overwrite: Boolean = false) { filePlugins.add(Pair(file, overwrite)) }

    /**
     * This will accept the mojang eula for you when start
     */
    fun acceptMojangEula() {acceptEula = true}

    /**
     * This will run when the task is called
     */
    override fun exec() {
        val startTime = System.currentTimeMillis()
        logger.lifecycle("===== Starting Server Setup =====")
        logger.lifecycle("Time: ${getCurrentTime()}")
        
        if (minecraftVersion == null) {
            throw IllegalArgumentException("Minecraft version is not set. Please set it with the 'minecraftVersion' property.")
        }

        logger.lifecycle("Minecraft Version: $minecraftVersion")
        logger.lifecycle("Server Type: ${serverType.name}")
        logger.lifecycle("Allocated RAM: $allowedRam")
        
        checkServerVersion()
        setup()
        createFolders()
        loadPlugin()

        logger.lifecycle("\n>> Downloading server JAR <<")
        logger.lifecycle("Server type: ${serverType.name.lowercase()}")
        logger.lifecycle("Minecraft version: $minecraftVersion")

        val download: DownloadResult? = downloadServerJar()

        if (download == null || download.resultType == DownloadResultType.SUCCESS) {
            val jarFile = download?.jarFile ?: File(workingDir, customJarName!!)
            logger.lifecycle("\n>> Server JAR ready: ${jarFile.name} (${formatFileSize(jarFile.length())}) <<")
            
            setClass(jarFile)

            val slitVersion = minecraftVersion!!.split(".")
            val mainVersion = slitVersion[1].toInt()
            val subVersion = slitVersion.getOrNull(2)?.toIntOrNull() ?: 0

            val jvmFlags = mutableListOf("-Xmx$allowedRam")
            if (serverType == ServerType.SPIGOT) jvmFlags.add("-DIReallyKnowWhatIAmDoingISwear")
            if (acceptEula) jvmFlags.add("-Dcom.mojang.eula.agree=true")
            
            logger.lifecycle("\n>> JVM Arguments: ${jvmFlags.joinToString(" ")} <<")
            jvmArgs = jvmFlags

            if (noGui) {
                if ((mainVersion == 15 && subVersion == 2) || mainVersion > 15) {
                    logger.lifecycle("Running with --nogui flag")
                    args("--nogui")
                } else {
                    logger.lifecycle("NoGUI flag not supported in this version")
                }
            }
            
            val setupTime = System.currentTimeMillis() - startTime
            logger.lifecycle("\n===== Server Setup Complete =====")
            logger.lifecycle("Setup completed in ${formatTime(setupTime)}")
            logger.lifecycle("Starting server...")
            logger.lifecycle("==============================\n")

            super.exec()
        } else {
            logger.error("\n>> Download failed <<")
            logger.error("Error: ${download.errorMessage}")
            logger.error("==============================\n")
        }
    }

    /**
     * This is checking the server version and if it exists.
     */
    private fun checkServerVersion() {
        logger.lifecycle("\n>> Checking server version compatibility <<")
        serverType.versions().let {
            if (it.isEmpty()) {
                logger.warn("Couldn't retrieve version list, assuming version is compatible")
            } else if (minecraftVersion !in it) {
                logger.error("Version $minecraftVersion not found in available versions: $it")
                throw VersionNotFoundException(minecraftVersion!!, it)
            } else {
                logger.lifecycle("Version $minecraftVersion is available for ${serverType.name}")
            }
        }
    }

    /**
     * This is downloading the server jar
     *
     * @return The download result if it was success or not
     */
    private fun downloadServerJar(): DownloadResult? = serverType.downloadJar(minecraftVersion!!, workingDir)

    /**
     * This method will load your plugin and download the rest from the websites or copy them
     */
    private fun loadPlugin() {
        logger.lifecycle("\n>> Loading project plugin <<")
        val pluginFile = TaskLib.findPluginJar(project, inputTask, this)
        val inServerFile = File(pluginDir!!, pluginFile.name)
        
        logger.lifecycle("Copying plugin: ${pluginFile.name} (${formatFileSize(pluginFile.length())})")
        logger.lifecycle("Destination: ${inServerFile.absolutePath}")
        
        pluginFile.copyTo(inServerFile, true)
        logger.lifecycle("Plugin copied successfully")

        if (filePlugins.isNotEmpty()) {
            logger.lifecycle("\n>> Copying additional plugins <<")
            copyFilePlugins()
        }
    }

    /**
     * This will copy the local plugins into the plugin folder
     */
    private fun copyFilePlugins() {
        filePlugins.forEachIndexed { index, (sourceFile, overwrite) ->
            val pluginFile = File(pluginDir!!, sourceFile.name)
            logger.lifecycle("${index+1}/${filePlugins.size}: Copying ${sourceFile.name} (${formatFileSize(sourceFile.length())})")
            
            if (pluginFile.exists() && !overwrite) {
                logger.lifecycle("  File already exists, skipping (overwrite=false)")
            } else {
                sourceFile.copyTo(pluginFile, overwrite)
                logger.lifecycle("  Copied successfully" + if(overwrite) " (overwritten)" else "")
            }
        }
    }

    /**
     * This will be creating all the folder for the server
     */
    private fun createFolders(){
        logger.lifecycle("\n>> Creating server directories <<")
        if (!runDir!!.exists()) {
            logger.lifecycle("Creating server directory: ${runDir!!.absolutePath}")
            runDir!!.mkdirs()
        } else {
            logger.lifecycle("Server directory exists: ${runDir!!.absolutePath}")
        }
        
        if (!pluginDir!!.exists()) {
            logger.lifecycle("Creating plugins directory: ${pluginDir!!.absolutePath}")
            pluginDir!!.mkdirs()
        } else {
            logger.lifecycle("Plugins directory exists: ${pluginDir!!.absolutePath}")
        }
        
        logger.lifecycle("Directories ready")
    }
    
    /**
     * Get current formatted time
     */
    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(Date())
    }
    
    /**
     * Format file size to human-readable format
     */
    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.2f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
    
    /**
     * Format time in milliseconds to human-readable format
     */
    private fun formatTime(timeInMs: Long): String {
        if (timeInMs < 1000) return "$timeInMs ms"
        return String.format("%.2f s", timeInMs / 1000.0)
    }
}

