package au.concepta.sakila.films

import au.concepta.sakila.Film
import au.concepta.sakila.database.tables.references.FILM
import au.concepta.sakila.infra.Database
import au.concepta.sakila.infra.STAFF_AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.filmModule() {
    val database: Database by dependencies

    routing {
        authenticate(STAFF_AUTH) {
            get("/films/search") {
                val title = call.request.queryParameters["title"] ?: ""
                val filmList = database.query { ctx ->
                    ctx.select(
                        FILM.FILM_ID,
                        FILM.TITLE,
                        FILM.DESCRIPTION,
                    )
                        .from(FILM)
                        .where(FILM.TITLE.likeIgnoreCase("%$title%"))
                        .mapNotNull { record ->
                            val filmId = record[FILM.FILM_ID] ?: return@mapNotNull null
                            val filmTitle = record[FILM.TITLE] ?: return@mapNotNull null
                            Film(
                                filmId = filmId,
                                title = filmTitle,
                                description = record[FILM.DESCRIPTION],
                            )
                        }
                }
                call.respond(filmList)
            }
        }
    }
}
