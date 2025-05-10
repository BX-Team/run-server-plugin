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
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

tasks {
    runServer {
        serverType(ServerType.DIVINEMC)
        minecraftVersion("1.21.5")
        noGui(true)
        acceptMojangEula()
    }
}
