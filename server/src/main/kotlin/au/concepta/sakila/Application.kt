package au.concepta.sakila

import au.concepta.sakila.database.tables.references.STORE
import au.concepta.sakila.infra.Database
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val database = Database(environment.config.config("database"))

    routing {
        get("/") {
            call.respondText("Online")
        }

        get("/stores") {
            val storeList = database.query { ctx ->
                ctx.selectFrom(STORE)
                    .joinToString { it.storeId.toString() }
            }
            call.respondText("Store IDs: $storeList")
        }
    }
}
