package au.concepta.sakila.stores

import au.concepta.sakila.database.tables.references.STORE
import au.concepta.sakila.infra.Database
import au.concepta.sakila.infra.STAFF_AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.storeModule() {
    val database: Database by dependencies

    routing {
        authenticate(STAFF_AUTH) {
            get("/stores") {
                val storeList = database.query { ctx ->
                    ctx.selectFrom(STORE)
                        .joinToString { it.storeId.toString() }
                }
                call.respondText("Store IDs: $storeList")
            }
        }
    }
}
