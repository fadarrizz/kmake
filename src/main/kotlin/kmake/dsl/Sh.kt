package kmake.dsl

fun sh(cmd: String) {
    val process = ProcessBuilder("sh", "-c", cmd).inheritIO().start()

    val result = process.waitFor()
    if (result != 0) {
        error("Command failed with exit code $result: $cmd")
    }
}
