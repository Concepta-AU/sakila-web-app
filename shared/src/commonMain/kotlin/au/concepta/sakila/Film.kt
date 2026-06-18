package au.concepta.sakila

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class Film(
    val filmId: Int,
    val title: String,
    val description: String?,
    val releaseYear: Int?,
    val languageId: Int,
    val originalLanguageId: Int?,
    val rentalDuration: Short,
    val rentalRate: Double,
    val length: Short?,
    val replacementCost: Double,
    val rating: String?,
    val specialFeatures: List<String?>?,
    val actors: List<Actor>,
)
