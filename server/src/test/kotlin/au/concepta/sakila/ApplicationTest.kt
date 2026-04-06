package au.concepta.sakila

import au.concepta.sakila.infra.LoginRequest
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.testing.*
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolute
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    companion object {
        private val postgres = PostgreSQLContainer("postgres:18-alpine")
            .withUsername("postgres")
            .withCopyFileToContainer(
                MountableFile.forHostPath(
                    findRepoRoot().resolve(
                        "docker/content/docker-entrypoint-initdb.d"
                    )
                ),
                "/docker-entrypoint-initdb.d"
            )
            .apply {
                start()
            }

        // we assume that the working directory is somewhere in the repository - not great, but hopefully safe enough
        fun findRepoRoot() = findRepoRoot(Paths.get(".").absolute())

        fun findRepoRoot(path: Path): Path = when {
            Files.exists(path.resolve("gradlew")) -> path
            path.parent == null -> throw IllegalStateException("Can not find repository root")
            else -> findRepoRoot(path.parent)
        }
    }

    fun ApplicationTestBuilder.configureApplication() {
        install(CallLogging)
        environment {
            config =
                ApplicationConfig("application.conf")
                    .mergeWith(
                        MapApplicationConfig(
                            "database.url" to postgres.jdbcUrl,
                            "database.username" to postgres.username,
                            "database.password" to postgres.password
                        )
                    )
        }
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
            }
        }
    }

    @Test
    fun testRoot() = testApplication {
        configureApplication()
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Online", response.bodyAsText())
    }

    @Test
    fun `all store IDs are listed`() = testApplication {
        configureApplication()
        val token = login(this)
        val response = client.get("/stores") {
            accept(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertEquals("Store IDs: ", body.substring(0, 11))
        val storeIds = body.substring(11).split(",").map { it.trim().toInt() }
        assertEquals((0..499).toList(), storeIds)
    }

    suspend fun login(builder: ApplicationTestBuilder): String {
        val response = builder.client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("valentin.yundt", "password"))
        }
        val data: User = response.body()
        return data.token
    }
}