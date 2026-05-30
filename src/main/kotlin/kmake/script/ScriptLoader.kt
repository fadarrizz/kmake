package kmake.script

import kmake.core.TaskRegistry
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

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
                    throw Exception("Script failed at runtime", returnValue.error)
                }

                return registry
            }

            is ResultWithDiagnostics.Failure -> {
                throw Exception(result.reports.joinToString { it.message })
            }
        }
    }
}
