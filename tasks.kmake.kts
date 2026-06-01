task("check") {
    description = "Compile and test"
    dependsOn("build", "test")
}

task("clean") {
    description = "Remove build output"
    action { sh("rm -rf build") }
}

task("build") {
    description = "Compile the project"
    action { sh("./gradlew assemble") }
}

task("test") {
    description = "Run the test suite"
    dependsOn("build")
    action { sh("./gradlew test") }
}
