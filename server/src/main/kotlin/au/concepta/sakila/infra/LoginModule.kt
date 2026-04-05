package au.concepta.sakila.infra

import at.favre.lib.crypto.bcrypt.BCrypt
import au.concepta.sakila.User
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

const val STAFF_AUTH = "sakila-staff"

fun Application.loginModule() {
    val database: Database by dependencies

    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    authentication {
        jwt(STAFF_AUTH) {
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
            val user = call.receive<LoginRequest>()
            val staff = database.query { ctx ->
                ctx.selectFrom(STAFF)
                    .where(STAFF.USERNAME.eq(user.username.lowercase()))
                    .fetch()
            }
            if (staff.size != 1 || // > 1 shouldn't be possible, but we rather fail than pretend
                !BCrypt.verifyer()
                    .verify(user.password.toCharArray(), staff[0]?.password!!.toCharArray())
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
            call.respond(
                User(
                    staff[0].firstName!!,
                    staff[0].lastName!!,
                    token
                )
            )
        }
    }
}

@Serializable
data class LoginRequest(val username: String, val password: String)
