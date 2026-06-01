package kmake.script

import kmake.KmakeException
import kmake.core.TaskRegistry
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class ScriptException(message: String, cause: Throwable) : KmakeException(message, cause)

class ScriptLoader {
    fun load(file: File): TaskRegistry {
        val registry = TaskRegistry()

        val compilationConfig = createJvmCompilationConfigurationFromTemplate<KmakeScript>()

        val evaluationConfig = ScriptEvaluationConfiguration {
            implicitReceivers(registry)
        }

        when (val result = BasicJvmScriptingHost().eval(file.toScriptSource(), compilationConfig, evaluationConfig)) {
            is ResultWithDiagnostics.Success -> {
                val returnValue = result.value.returnValue
                if (returnValue is ResultValue.Error) {
                    throw ScriptException("Script failed at runtime", returnValue.error)
                }

                return registry
            }

            is ResultWithDiagnostics.Failure -> {
                val msg = result.reports
                    .filter { it.severity >= ScriptDiagnostic.Severity.ERROR }
                    .joinToString { it.message }
                throw KmakeException(msg)
            }
        }
    }
}
