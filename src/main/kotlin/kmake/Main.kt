package kmake

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kmake.cli.discoverTasksFile
import kmake.core.DependencyResolver
import kmake.core.TaskExecutor
import kmake.script.ScriptLoader
import java.io.File

class Kmake : CliktCommand() {
    val task by argument().optional()
    val list by option("--list").flag()
    val file by option("-f", "--file")

    override fun run() {
        val scriptFile = file?.let { File(it) } ?: discoverTasksFile() ?: error("No tasks file found")
        val registry = ScriptLoader().load(scriptFile)
        val taskName = task

        when {
            list -> {
                for (t in registry.all) {
                    var listing = t.name
                    if (t.description.isNotEmpty()) {
                        listing += ": ${t.description}"
                    }
                    echo(listing)
                }
            }
            taskName != null -> TaskExecutor().execute(DependencyResolver(registry).resolve(taskName))
            else -> echo(message = "No task specified", err = true)
        }
    }
}

fun main(args: Array<String>) = Kmake().main(args)
