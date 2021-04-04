package cat.covidcontact.server.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun runGet(method: () -> Any?) = runRequest(HttpStatus.OK, method)

fun runPost(method: () -> Any?) = runRequest(HttpStatus.CREATED, method)

fun runPut(method: () -> Any?) = runRequest(HttpStatus.ACCEPTED, method)

fun runDelete(method: () -> Any?) = runRequest(HttpStatus.ACCEPTED, method)

fun runRequest(status: HttpStatus, method: () -> Any?) = try {
    val result = method()
    result?.let {
        ResponseEntity(result, status)
    } ?: ResponseEntity(status)
} catch (e: CovidContactException) {
    e.toResponseEntity()
}
