package cat.covidcontact.server.security

import java.io.File

object SecurityConstants {
    const val EXPIRATION_TIME = 86400000
    const val HEADER_STRING = "Authorization"
    const val TOKEN_PREFIX = "Bearer "

    val PRIVATE_KEY = File("key.txt").useLines { it.first() }
}