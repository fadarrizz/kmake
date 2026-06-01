package kmake.dsl

import kmake.KmakeException

class CommandFailedException(message: String) : KmakeException(message)

fun sh(cmd: String) {
    val process = ProcessBuilder("sh", "-c", cmd).inheritIO().start()

    val result = process.waitFor()
    if (result != 0) {
        throw CommandFailedException("Command failed with exit code $result: $cmd")
    }
}
