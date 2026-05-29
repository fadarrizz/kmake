package kmake.dsl

import kmake.core.Task
import kmake.core.TaskRegistry
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TaskDslTest {
    @Test
    fun `makes and registers task`() {
        val registry = TaskRegistry()
        var ran = false

        registry.task("build") {
            description = "x"
            dependsOn("clean", "move")
            action { ran = true }
        }
        
        val task = registry["build"]
        assertIs<Task>(task)
        assertEquals("build", task.name)
        assertEquals("x", task.description)
        assertEquals(2, task.dependencies.count())
        assertEquals("clean", task.dependencies[0])
        assertEquals("move", task.dependencies[1])
        runBlocking { task.action() }
        assertEquals(true, ran)
    }
}
