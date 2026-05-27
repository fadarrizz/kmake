package kmake.core

class TaskRegistry {
    private val tasks = mutableMapOf<String, Task>()
    val all: Collection<Task> get() = tasks.values.toList()

    operator fun get(name: String): Task? = tasks[name]

    fun register(task: Task) {
        require(task.name !in tasks) { "Task '${task.name}' is already defined" }

        tasks[task.name] = task
    }
}
