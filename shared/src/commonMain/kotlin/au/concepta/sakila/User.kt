package au.concepta.sakila

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val token: String,
)
