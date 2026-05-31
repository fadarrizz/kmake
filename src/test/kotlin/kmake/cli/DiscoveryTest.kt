package kmake.cli

import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiscoveryTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `file in current dir`() {
        val expected = File(tempDir, "tasks.kmake.kts")
        expected.writeText("")

        val actual = discoverTasksFile(tempDir)

        assertEquals(expected.canonicalFile, actual?.canonicalFile)
    }

    @Test
    fun `file in an ancestor`() {
        val expected = File(tempDir, "tasks.kmake.kts")
        expected.writeText("")

        val start = File(tempDir, "a/")
        start.mkdirs()

        val actual = discoverTasksFile(start)

        assertEquals(expected.canonicalFile, actual?.canonicalFile)
    }

    @Test
    fun `file does not exist`() {
        assertNull(discoverTasksFile(tempDir))
    }

    @Test
    fun `find nearest file in case of duplicates`() {
        val furthestFile = File(tempDir, "tasks.kmake.kts")
        furthestFile.writeText("")

        val start = File(tempDir, "a")
        start.mkdirs()

        val nearestFile = File(start, "tasks.kmake.kts")
        nearestFile.writeText("")

        val actual = discoverTasksFile(start)

        assertEquals(nearestFile.canonicalFile, actual?.canonicalFile)
    }
}
