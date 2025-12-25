rootProject.name = "korm-ksp"

include(
    ":korm-ksp-annotations",
    ":korm-ksp-core",
    ":korm-ksp-processor",
    ":korm-ksp-runtime",
    ":korm-ksp-cache",
    ":korm-ksp-migrations",
    ":korm-ksp-validation",
    ":benchmarks",
    ":examples:example-basic",
    ":examples:example-relationships",
    ":examples:example-rest-api",
    ":examples:example-spring",
    ":examples:example-android",
    ":examples:example-enterprise",
    ":examples:example-validation"
)