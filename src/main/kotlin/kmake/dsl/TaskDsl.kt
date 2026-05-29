package kmake.dsl

import kmake.core.Task
import kmake.core.TaskRegistry

fun TaskRegistry.task(name: String, block: TaskBuilder.() -> Unit) {
    val task = TaskBuilder().apply(block).build(name)
    register(task)
}

class TaskBuilder {
    var description: String = ""
    private val dependencies = mutableListOf<String>()
    private var actionBlock: suspend () -> Unit = {}

    fun dependsOn(vararg names: String) {
        dependencies.addAll(names)
    }

    fun action(block: suspend () -> Unit) {
        actionBlock = block
    }

    fun build(name: String): Task {
        return Task(name, description, dependencies.toList(), actionBlock)
    }
}
