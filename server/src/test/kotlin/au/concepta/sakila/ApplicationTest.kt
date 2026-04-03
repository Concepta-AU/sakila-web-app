package au.concepta.sakila

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
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
        environment {
            config = config.mergeWith(
                MapApplicationConfig(
                    "database.url" to postgres.jdbcUrl,
                    "database.username" to postgres.username,
                    "database.password" to postgres.password
                )
            )
        }
        application {
            module()
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
        val response = client.get("/stores")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertEquals("Store IDs: ", body.substring(0, 11))
        val storeIds = body.substring(11).split(",").map { it.trim().toInt() }
        assertEquals((0..499).toList(), storeIds)
    }
}