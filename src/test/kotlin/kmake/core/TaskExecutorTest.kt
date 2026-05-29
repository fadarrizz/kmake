package kmake.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TaskExecutorTest {
    @Test
    fun `executes a task`() {
        val executor = TaskExecutor()
        val executed = mutableListOf<String>()

        val a = Task("a", action = { executed.add("a") })
        val b = Task("b", action = { executed.add("b") })
        val c = Task("c", action = { executed.add("c") })

        executor.execute(listOf(a, b, c))

        assertEquals(listOf("a", "b", "c"), executed)
    }

    @Test
    fun `stops execution after failing task`() {
        val executor = TaskExecutor()
        val executed = mutableListOf<String>()

        val a = Task("a", action = { executed.add("a"); throw Exception() })
        val b = Task("b", action = { executed.add("b") })
        val c = Task("c", action = { executed.add("c") })

        assertFails { executor.execute(listOf(a, b, c)) }
        assertEquals(listOf("a"), executed)
    }
}
