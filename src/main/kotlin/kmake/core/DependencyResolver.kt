package kmake.core

enum class Status {
    PROCESSING, PROCESSED
}

class DependencyCycleException(message: String): Exception(message)

class DependencyResolver(private val registry: TaskRegistry) {
    fun resolve(target: String): List<Task> = Resolution(registry).resolve(target)

    private class Resolution(private val registry: TaskRegistry) {
        val result = mutableListOf<Task>()
        val seen = mutableMapOf<String, Status>()
        val path = mutableListOf<Task>()

        fun resolve(target: String): List<Task> {
            val task = registry[target]
            require(task != null) { "Task '$target' is not registered" }

            dfs(task)

            return result
        }

        private fun dfs(task: Task) {
            when (seen[task.name]) {
                Status.PROCESSING -> {
                    val start = path.indexOfFirst { it.name == task.name }
                    val cyclePath = path
                        .subList(start, path.size)
                        .joinToString(" -> ") { it.name } + " -> ${task.name}"
                    throw DependencyCycleException("Dependency cycle detected: $cyclePath")
                }
                Status.PROCESSED -> return
                null -> {}
            }

            seen[task.name] = Status.PROCESSING
            path.add(task)

            for (dep in task.dependencies) {
                val depTask = registry[dep]
                require(depTask != null) { "Task '${task.name}' depends on unknown task '$dep'" }
                dfs(depTask)
            }

            seen[task.name] = Status.PROCESSED
            // Avoided `path.removeLast()` because of JDK 21 addition of `SequencedCollection.removeLast()`, 
            // which collides with Kotlin's stdlib `removeLast()`.
            path.removeAt(path.lastIndex)

            result.add(task)
        }
    }
}
