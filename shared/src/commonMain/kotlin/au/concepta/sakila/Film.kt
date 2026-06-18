package au.concepta.sakila

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class Film(
    val filmId: Int,
    val title: String,
    val description: String?,
)
