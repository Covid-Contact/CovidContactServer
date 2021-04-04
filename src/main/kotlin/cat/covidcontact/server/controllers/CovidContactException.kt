package cat.covidcontact.server.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CovidContactException(
    val code: String,
    val name: String,
    val responseCode: HttpStatus
) : Exception() {

    fun toResponseEntity() = ResponseEntity<Any>("$code $name", responseCode)
}
