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
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly(libs.spigot.api)
}

tasks {
    runServer {
        serverType(ServerType.PAPER)
        serverVersion("1.21.7")
        noGui(true)
        acceptMojangEula()

        downloadPlugins {
            url("https://download.luckperms.net/1587/bukkit/loader/LuckPerms-Bukkit-5.5.2.jar")
            jenkins("https://ci.athion.net", "FastAsyncWorldEdit", Regex("Bukkit"))
        }
    }
}
