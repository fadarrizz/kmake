package kmake.core

import kmake.KmakeException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TaskRegistryTest {
    @Test
    fun `look up task by name`() {
        val registry = TaskRegistry()

        assertEquals(null, registry["a task"])

        val task = Task("a task")
        registry.register(task)

        assertEquals(task, registry["a task"])
    }

    @Test
    fun `throws when name already registered`() {
        val registry = TaskRegistry()

        val task1 = Task("a task")
        registry.register(task1)

        val task2 = Task("a task")
        assertFailsWith<KmakeException> {
            registry.register(task2)
        }
    }

    @Test
    fun `returns all registered tasks`() {
        val registry = TaskRegistry()

        val task1 = Task("a task")
        registry.register(task1)

        assertEquals(1, registry.all.count())

        val task2 = Task("another task")
        registry.register(task2)

        assertEquals(2, registry.all.count())
    }
}
