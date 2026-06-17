package au.concepta.sakila

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class Store(
    val storeId: Int,
    val address: String,
    val address2: String?,
    val district: String,
    val postalCode: String?,
    val phone: String,
)
