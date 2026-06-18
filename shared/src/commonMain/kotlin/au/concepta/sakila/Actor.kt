package au.concepta.sakila

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class Actor(
    val actorId: Int,
    val firstName: String,
    val lastName: String,
)
