package kmake

import kmake.script.ScriptLoader
import java.io.File

fun main() {
    val registry = ScriptLoader().load(File("tasks.kmake.kts"))

    println(registry.all.map { it.name })
}
