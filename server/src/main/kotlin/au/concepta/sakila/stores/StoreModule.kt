package au.concepta.sakila.stores

import au.concepta.sakila.Store
import au.concepta.sakila.database.tables.references.ADDRESS
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
                    ctx.select(
                        STORE.STORE_ID,
                        ADDRESS.ADDRESS_,
                        ADDRESS.ADDRESS2,
                        ADDRESS.DISTRICT,
                        ADDRESS.POSTAL_CODE,
                        ADDRESS.PHONE,
                    )
                        .from(STORE)
                        .join(ADDRESS).on(STORE.ADDRESS_ID.eq(ADDRESS.ADDRESS_ID))
                        .mapNotNull { record ->
                            val storeId = record[STORE.STORE_ID] ?: return@mapNotNull null
                            val address = record[ADDRESS.ADDRESS_] ?: return@mapNotNull null
                            val district = record[ADDRESS.DISTRICT] ?: return@mapNotNull null
                            val phone = record[ADDRESS.PHONE] ?: return@mapNotNull null
                            Store(
                                storeId = storeId,
                                address = address,
                                address2 = record[ADDRESS.ADDRESS2],
                                district = district,
                                postalCode = record[ADDRESS.POSTAL_CODE],
                                phone = phone,
                            )
                        }
                }
                call.respond(storeList)
            }
        }
    }
}
