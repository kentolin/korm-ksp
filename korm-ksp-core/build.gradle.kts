dependencies {
    api(libs.kotlin.stdlib)
    api(libs.hikari)
    api(libs.slf4j.api)

    implementation(libs.postgresql)
    implementation(libs.mysql)
    implementation(libs.sqlite)
    implementation(libs.h2)
}