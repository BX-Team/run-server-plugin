# run-server-plugin

![GitHub License](https://img.shields.io/github/license/BX-Team/run-server-plugin)
![GitHub top language](https://img.shields.io/github/languages/top/BX-Team/run-server-plugin?logo=kotlin&color=blue)
[![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/org.bxteam.runserver?label=Gradle%20Plugin%20Portal&color=007ec6)](https://plugins.gradle.org/plugin/org.bxteam.runserver)

Gradle plugin for running Minecraft server instances in your IDE 

## Installation

### build.gradle

```groovy
plugins {
    id 'org.bxteam.runserver' version '1.2.1'
}
```

### build.gradle.kts

```kotlin
plugins {
    id("org.bxteam.runserver") version "1.2.1"
}
```

# Usage

## Basic

```kotlin
tasks {
    runServer {
        serverType(ServerType.PAPER)
        serverVersion("1.21.7")
        noGui(true)
        acceptMojangEula()

        downloadPlugins {
            url("https://download.luckperms.net/1581/bukkit/loader/LuckPerms-Bukkit-5.4.164.jar")
        }
    }
}
```

## Advanced

```kotlin
tasks {
    runServer {
        // Server type and version
        serverType(ServerType.PAPER)
        serverVersion("1.21.7")
        
        // RAM configuration
        allowedRam(4, RamAmount.GB) // Allocate 4GB of RAM
        // or
        allowedRam(2048, RamAmount.MB) // Allocate 2048MB of RAM
        
        // GUI settings
        noGui(true) // Disable GUI (recommended for servers)
        
        // EULA acceptance
        acceptMojangEula() // Automatically accept Mojang EULA
        
        // Plugin downloads using the new DSL
        downloadPlugins {
            // Modrinth plugins
            modrinth("worldedit", "7.3.12")
            
            // GitHub releases
            github("NEZNAMY", "TAB", "5.2.0", " TAB.v5.2.0.jar ")
            
            // Hangar plugins
            hangar("squaremap", "1.3.5")
            
            // Jenkins artifacts (only latest builds)
            jenkins("https://ci.athion.net", "FastAsyncWorldEdit", Regex("Bukkit"))
            
            // Direct URL downloads
            url("https://download.luckperms.net/1581/bukkit/loader/LuckPerms-Bukkit-5.4.164.jar")
        }
        
        // Local plugin files
        // Single file
        filePlugin(File("plugins/my-plugin.jar"))
        // With overwrite option
        filePlugin(File("plugins/another-plugin.jar"), overwrite = true)
        
        // Multiple files
        filePlugins(
            File("plugins/plugin1.jar"),
            File("plugins/plugin2.jar")
        )
        
        // List of files
        filePlugins(listOf(
            File("plugins/plugin3.jar"),
            File("plugins/plugin4.jar")
        ))
        
        // List of files with overwrite option
        filePlugins(listOf(
            File("plugins/plugin5.jar") to true,  // Will overwrite
            File("plugins/plugin6.jar") to false  // Won't overwrite
        ))
        
        // Custom server folder
        serverFolderName("my-server") // Creates server in 'my-server' directory
        // or with dynamic name
        serverFolderName { "server-${minecraftVersion}-${serverType.name.lowercase()}" }
        // or with full path control
        serverFolder { File(projectDir, "servers/${minecraftVersion}/${serverType.name.lowercase()}") }
        
        // Version-specific folders
        perVersionFolder(true) // Creates separate folders for each Minecraft version
        
        // Custom input task (if you want to use a different task's output)
        inputTask(tasks.named("shadowJar")) // Use shadowJar task output instead of jar
    }
}
```

### Supported Jar Types

We're using [MCJars API](https://mcjars.app/api) to download and check the server jar. We support the following jar types:

- Spigot
- Paper
- Pufferfish
- Purpur
- Canvas
- DivineMC
- Leaf
- Leaves
- Bungeecord
- Velocity
- Velocity-CTD
- Waterfall
