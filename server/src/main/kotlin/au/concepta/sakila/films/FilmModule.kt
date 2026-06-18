package au.concepta.sakila.films

import au.concepta.sakila.Actor
import au.concepta.sakila.Film
import au.concepta.sakila.database.tables.references.ACTOR
import au.concepta.sakila.database.tables.references.FILM
import au.concepta.sakila.database.tables.references.FILM_ACTOR
import au.concepta.sakila.database.tables.references.FILM_IN_STOCK
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
                val storeId = call.request.queryParameters["storeId"]?.toIntOrNull()
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
                        ACTOR.ACTOR_ID,
                        ACTOR.FIRST_NAME,
                        ACTOR.LAST_NAME,
                    )
                        .from(FILM)
                        .leftJoin(FILM_ACTOR).on(FILM_ACTOR.FILM_ID.eq(FILM.FILM_ID))
                        .leftJoin(ACTOR).on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
                        .where(FILM.TITLE.likeIgnoreCase("%$title%"))
                        .fetch()
                        .groupBy { it[FILM.FILM_ID] }
                        .values
                        .mapNotNull { records ->
                            val first = records.first()
                            val filmId = first[FILM.FILM_ID] ?: return@mapNotNull null
                            val filmTitle = first[FILM.TITLE] ?: return@mapNotNull null
                            val languageId = first[FILM.LANGUAGE_ID] ?: return@mapNotNull null
                            val rentalDuration = first[FILM.RENTAL_DURATION] ?: return@mapNotNull null
                            val rentalRate = first[FILM.RENTAL_RATE] ?: return@mapNotNull null
                            val replacementCost = first[FILM.REPLACEMENT_COST] ?: return@mapNotNull null
                            val actors = records.mapNotNull { record ->
                                val actorId = record[ACTOR.ACTOR_ID] ?: return@mapNotNull null
                                val firstName = record[ACTOR.FIRST_NAME] ?: return@mapNotNull null
                                val lastName = record[ACTOR.LAST_NAME] ?: return@mapNotNull null
                                Actor(actorId = actorId, firstName = firstName, lastName = lastName)
                            }
                            val copiesAvailable = if (storeId != null) {
                                ctx.selectFrom(FILM_IN_STOCK.call(filmId, storeId))
                                    .fetch()
                                    .size
                            } else null
                            Film(
                                filmId = filmId,
                                title = filmTitle,
                                description = first[FILM.DESCRIPTION],
                                releaseYear = first[FILM.RELEASE_YEAR],
                                languageId = languageId,
                                originalLanguageId = first[FILM.ORIGINAL_LANGUAGE_ID],
                                rentalDuration = rentalDuration,
                                rentalRate = rentalRate.toDouble(),
                                length = first[FILM.LENGTH],
                                replacementCost = replacementCost.toDouble(),
                                rating = first[FILM.RATING]?.literal,
                                specialFeatures = first[FILM.SPECIAL_FEATURES]?.toList(),
                                actors = actors,
                                copiesAvailable = copiesAvailable,
                            )
                        }
                }
                call.respond(filmList)
            }
        }
    }
}
