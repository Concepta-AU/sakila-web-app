package au.concepta.sakila.stores

import au.concepta.sakila.database.tables.references.STORE
import au.concepta.sakila.infra.Database
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.storeModule() {
    val database: Database by dependencies

    routing {
        get("/stores") {
            val storeList = database.query { ctx ->
                ctx.selectFrom(STORE)
                    .joinToString { it.storeId.toString() }
            }
            call.respondText("Store IDs: $storeList")
        }
    }
}
