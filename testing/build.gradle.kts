import org.bxteam.runserver.ServerType

plugins {
    id("java")
    id("org.bxteam.runserver")
}

group = "org.bxteam"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

tasks {
    runServer {
        serverType(ServerType.PAPER)
        minecraftVersion("1.21.5")
        noGui(true)
        acceptMojangEula()
        
        downloadPlugins {
            url("https://download.luckperms.net/1581/bukkit/loader/LuckPerms-Bukkit-5.4.164.jar")
        }
    }
}
