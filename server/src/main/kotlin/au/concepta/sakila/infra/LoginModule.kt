package au.concepta.sakila.infra

import at.favre.lib.crypto.bcrypt.BCrypt
import au.concepta.sakila.database.tables.references.STAFF
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.di.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

fun Application.loginModule() {
    val database: Database by dependencies

    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    routing {
        post("/login") {
            val user = call.receive<User>()
            val passwordHash = database.query { ctx ->
                ctx.selectFrom(STAFF)
                    .where(STAFF.USERNAME.eq(user.username.lowercase()))
                    .fetch(STAFF.PASSWORD)
            }
            if (passwordHash.size != 1 || // > 1 shouldn't be possible, but we rather fail than pretend
                !BCrypt.verifyer()
                    .verify(user.password.toCharArray(), passwordHash[0]!!.toCharArray())
                    .verified
            ) {
                call.respond(HttpStatusCode.Unauthorized, "Login failed")
            }
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(hashMapOf("token" to token))
        }
    }
}

@Serializable
data class User(val username: String, val password: String)
