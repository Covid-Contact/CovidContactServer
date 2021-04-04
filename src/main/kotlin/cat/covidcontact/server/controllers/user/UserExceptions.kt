package cat.covidcontact.server.controllers.user

import cat.covidcontact.server.controllers.CovidContactException
import org.springframework.http.HttpStatus

object UserExceptions {
    val userExistingException = CovidContactException(
        "U1",
        "User existing",
        HttpStatus.BAD_REQUEST
    )

    val invalidIdException = CovidContactException(
        "U2",
        "Invalid id",
        HttpStatus.BAD_REQUEST
    )

    val userNotExistingException = CovidContactException(
        "U3",
        "User not existing",
        HttpStatus.NOT_FOUND
    )
}