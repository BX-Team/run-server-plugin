package org.bxteam.runserver

import org.gradle.api.Plugin
import org.gradle.api.Project

class RunServerPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.tasks.register("runServer", RunServerTask::class.java) {
      group = "run-server-plugin"
      description = "This task will run an minecraft server in your IDE"
    }
  }
}
