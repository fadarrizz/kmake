package kmake.core

import kotlinx.coroutines.runBlocking

class TaskExecutor {
    fun execute(tasks: List<Task>) {
        runBlocking {
            for (task in tasks) {
                println("Start: ${task.name}")
                task.action()
                println("Done: ${task.name}")
            }
        }
    }
}
