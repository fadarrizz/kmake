task("build") {
    description = "compiles the thing"
    action { println("building...") }
}

task("test") {
    dependsOn("build")
    action { println("testing...") }
}

task("greet") {
    action {
        sh("echo building && echo done")
        sh("false")
    }
}
