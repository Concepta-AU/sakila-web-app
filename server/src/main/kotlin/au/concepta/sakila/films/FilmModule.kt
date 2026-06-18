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
                        FILM.RELEASE_YEAR,
                        FILM.LANGUAGE_ID,
                        FILM.ORIGINAL_LANGUAGE_ID,
                        FILM.RENTAL_DURATION,
                        FILM.RENTAL_RATE,
                        FILM.LENGTH,
                        FILM.REPLACEMENT_COST,
                        FILM.RATING,
                        FILM.SPECIAL_FEATURES,
                    )
                        .from(FILM)
                        .where(FILM.TITLE.likeIgnoreCase("%$title%"))
                        .mapNotNull { record ->
                            val filmId = record[FILM.FILM_ID] ?: return@mapNotNull null
                            val filmTitle = record[FILM.TITLE] ?: return@mapNotNull null
                            val languageId = record[FILM.LANGUAGE_ID] ?: return@mapNotNull null
                            val rentalDuration = record[FILM.RENTAL_DURATION] ?: return@mapNotNull null
                            val rentalRate = record[FILM.RENTAL_RATE] ?: return@mapNotNull null
                            val replacementCost = record[FILM.REPLACEMENT_COST] ?: return@mapNotNull null
                            Film(
                                filmId = filmId,
                                title = filmTitle,
                                description = record[FILM.DESCRIPTION],
                                releaseYear = record[FILM.RELEASE_YEAR],
                                languageId = languageId,
                                originalLanguageId = record[FILM.ORIGINAL_LANGUAGE_ID],
                                rentalDuration = rentalDuration,
                                rentalRate = rentalRate.toDouble(),
                                length = record[FILM.LENGTH],
                                replacementCost = replacementCost.toDouble(),
                                rating = record[FILM.RATING]?.literal,
                                specialFeatures = record[FILM.SPECIAL_FEATURES]?.toList(),
                            )
                        }
                }
                call.respond(filmList)
            }
        }
    }
}
