package kmake.core

import kmake.KmakeException

class TaskRegistry {
    private val tasks = mutableMapOf<String, Task>()
    val all: Collection<Task> get() = tasks.values.toList()

    operator fun get(name: String): Task? = tasks[name]

    fun register(task: Task) {
        if (task.name in tasks) throw KmakeException("Task '${task.name}' is already defined")

        tasks[task.name] = task
    }
}
