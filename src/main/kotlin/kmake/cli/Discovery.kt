package kmake.cli

import java.io.File

fun discoverTasksFile(start: File = File(".").canonicalFile): File? {
    return generateSequence(start) { it.parentFile }
        .map { File(it, "tasks.kmake.kts") }
        .firstOrNull { it.isFile }
}
