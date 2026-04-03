package au.concepta.sakila.infra

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

fun Application.databaseModule() {
    val config = environment.config.config("database")
    dependencies {
        provide<Database> { Database(config) }
    }
}

class Database(config: ApplicationConfig) {
    val connectionPool: HikariDataSource

    init {
        val databaseConfig = DatabaseConfig(config)
        connectionPool = HikariDataSource(
            HikariConfig()
                .apply {
                    jdbcUrl = databaseConfig.url
                    username = databaseConfig.username
                    maximumPoolSize = databaseConfig.poolSize
                    password = databaseConfig.password
                    isAutoCommit = false
                }
                .also { it.validate() })
    }

    suspend fun <T> query(block: (DSLContext) -> T): T = withContext(Dispatchers.IO) {
        block(DSL.using(connectionPool, SQLDialect.POSTGRES))
    }

    suspend fun <T> exec(block: (DSLContext) -> T): T = withContext(Dispatchers.IO) {
        DSL.using(connectionPool, SQLDialect.POSTGRES)
            .transactionResultAsync { config -> block(DSL.using(config)) }.await()
    }
}

internal data class DatabaseConfig(
    val url:String, val username: String, val password: String, val poolSize: Int
) {
    constructor(config: ApplicationConfig): this(
        url = config.property("url").getString(),
        username= config.property("username").getString(),
        password = config.property("password").getString(),
        poolSize = config.propertyOrNull("poolSize")?.getString()?.toInt() ?: 10
    )
}