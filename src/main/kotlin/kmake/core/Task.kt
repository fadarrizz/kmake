package kmake.core

data class Task(
    val name: String,
    val description: String = "",
    val dependencies: List<String> = emptyList(),
    val action: suspend () -> Unit = {},
)
