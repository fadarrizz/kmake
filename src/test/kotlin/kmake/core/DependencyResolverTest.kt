package kmake.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class DependencyResolverTest {
    @Test
    fun `throws when task is not registered`() {
        val registry = TaskRegistry()
        val resolver = DependencyResolver(registry)

        assertFailsWith<UnknownTaskException> { resolver.resolve("a") }
    }

    @Test
    fun `task without dependencies`() {
        val registry = TaskRegistry()
        val resolver = DependencyResolver(registry)

        val task = Task("a")
        registry.register(task)

        val result = resolver.resolve("a")

        assertEquals(1, result.count())
        assertSame(task, result[0])
    }

    @Test
    fun `task with one dependency`() {
        val registry = TaskRegistry()
        val resolver = DependencyResolver(registry)

        val task1 = Task("a")
        val task2 = Task("b", "", listOf("a"))
        registry.register(task1)
        registry.register(task2)

        val result = resolver.resolve("b")

        assertEquals(2, result.count())
        assertSame(task1, result[0])
        assertSame(task2, result[1])
    }

    @Test
    fun `task with two-level deep dependencies`() {
        val registry = TaskRegistry()
        val resolver = DependencyResolver(registry)

        val a = Task("a")
        val b = Task("b", "", listOf("a"))
        val c = Task("c", "", listOf("b"))
        registry.register(a)
        registry.register(b)
        registry.register(c)

        val result = resolver.resolve("c")

        assertEquals(3, result.count())
        assertSame(a, result[0])
        assertSame(b, result[1])
        assertSame(c, result[2])
    }

    @Test
    fun `handles diamonds`() {
        val registry = TaskRegistry()
        val resolver = DependencyResolver(registry)

        val app = Task("app", "", listOf("lib1", "lib2"))
        val lib1 = Task("lib1", "", listOf("core"))
        val lib2 = Task("lib2", "", listOf("core"))
        val core = Task("core")
        registry.register(app)
        registry.register(lib1)
        registry.register(lib2)
        registry.register(core)

        val result = resolver.resolve("app")

        assertEquals(4, result.count())
        assertSame(core, result[0])
        assertSame(lib1, result[1])
        assertSame(lib2, result[2])
        assertSame(app, result[3])
    }

    @Test
    fun `throws when dependency is not registered`() {
        val registry = TaskRegistry()
        val resolver = DependencyResolver(registry)

        val task = Task("a", "", listOf("x"))
        registry.register(task)

        assertFailsWith<UnknownTaskException> { resolver.resolve("a") }
    }

    @Test
    fun `detects cycles`() {
        val registry = TaskRegistry()
        val resolver = DependencyResolver(registry)

        val a = Task("a", "", listOf("b"))
        val b = Task("b", "", listOf("c"))
        val c = Task("c", "", listOf("b", "done"))
        val done = Task("done")
        registry.register(a)
        registry.register(b)
        registry.register(c)
        registry.register(done)

        val ex = assertFailsWith<DependencyCycleException> { resolver.resolve("a") }
        assertEquals("Dependency cycle detected: b -> c -> b", ex.message)
    }
}
