plugins {
    alias(libs.plugins.kotlinJvm)
    kotlin("plugin.serialization").version("2.2.20")
    alias(libs.plugins.ktor)
    application
}

group = "au.concepta.sakila"
version = "0.0.1"

application {
    mainClass.set("au.concepta.sakila.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(projects.databaseAccess)
    implementation(libs.hikari)
    implementation(libs.logback)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.serverAuth)
    implementation(libs.ktor.serverAuthJwt)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverDi)
    implementation(libs.ktor.serverNetty)
    implementation(libs.postgres.driver)
    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-core:3.4.2")
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.testcontainers.postgres)
}