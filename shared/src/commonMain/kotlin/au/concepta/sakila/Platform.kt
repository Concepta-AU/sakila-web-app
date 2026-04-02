package au.concepta.sakila

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform