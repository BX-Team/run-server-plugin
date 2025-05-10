package org.bxteam.runserver.util.lib

import org.bxteam.runserver.RunServerTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File

object TaskLib {
    object TaskNames {
        const val JAR = "jar"
        const val SHADOW = "shadowJar"
        const val REMAP = "remap"
    }

    /**
     * Gets the projects plugin file by check what task to use and the depending on them
     *
     * @param project The gradle project
     * @param inputTask The input task that can be specified by the user
     * @param runServerTask The run server task
     * @return This will return the projects plugin file
     */
    fun findPluginJar(project: Project, inputTask: TaskProvider<*>?, runServerTask: RunServerTask): File {
        if (inputTask?.isPresent == true) {
            return inputTask.get().outputs.files.singleFile
        }
        if (TaskNames.REMAP in project.tasks.names) {
            val task = project.tasks.named(TaskNames.REMAP)
            return task.get().outputs.files.singleFile
        }
        if (TaskNames.SHADOW in project.tasks.names) {
            val task = project.tasks.named(TaskNames.SHADOW)
            return task.get().outputs.files.singleFile
        }
        val jar = project.tasks.named(TaskNames.JAR)
        return jar.get().outputs.files.singleFile
    }
}
