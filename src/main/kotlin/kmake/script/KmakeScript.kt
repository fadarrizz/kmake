package kmake.script

import kmake.core.TaskRegistry
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
    fileExtension = "kmake.kts",
    compilationConfiguration = KmakeScriptConfiguration::class,
)
abstract class KmakeScript

object KmakeScriptConfiguration: ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
    defaultImports("kmake.core.Task", "kmake.dsl.task")
    implicitReceivers(TaskRegistry::class)
}) {
    private fun readResolve(): Any = KmakeScriptConfiguration
}
