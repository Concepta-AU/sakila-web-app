plugins {
    alias(libs.plugins.kotlinJvm)
    `java-library`
}

group = "au.concepta.sakila"
version = "0.0.1"

dependencies {
    api("org.jooq:jooq:3.20.8")
    testImplementation(libs.kotlin.testJunit)
}
