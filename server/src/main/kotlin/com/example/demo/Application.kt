package com.example.demo

import au.concepta.sakila.database.tables.references.STORE
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jooq.impl.DSL
import java.util.Properties

val properties = Properties()

fun main() {
    properties.load(Application::class.java.getResourceAsStream("/config.properties"))
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        get("/stores") {
            DSL.using(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
            ).use { ctx ->
                val storeList = ctx.selectFrom(STORE)
                    .joinToString { it.storeId.toString() }
                call.respondText("Store IDs: $storeList")
            }
        }
    }
}
