package kmake

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
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
    val list by option("-l", "--list").flag()
    val file by option("-f", "--file")
    val verbose by option("-v", "--verbose").flag()

    override fun run() {
        try {
            val scriptFile =
                file?.let { File(it) } ?: discoverTasksFile() ?: throw KmakeException("No tasks file found")
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
                else -> throw KmakeException("No task specified")
            }
        } catch (e: KmakeException) {
            echo(e.message, err = true)
            if (verbose) echo(e.stackTraceToString(), err = true)
            throw ProgramResult(1)
        }
    }
}

fun main(args: Array<String>) = Kmake().main(args)
