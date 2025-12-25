plugins {
    alias(libs.plugins.ksp)
    application
}

application {
    mainClass.set("com.korm.examples.ksp.basic.MainKt")
}

dependencies {
    implementation(project(":korm-ksp-annotations"))
    implementation(project(":korm-ksp-runtime"))
    implementation(project(":korm-ksp-core"))
    ksp(project(":korm-ksp-processor"))

    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)
    implementation(libs.postgresql)
    implementation(libs.h2)
}
