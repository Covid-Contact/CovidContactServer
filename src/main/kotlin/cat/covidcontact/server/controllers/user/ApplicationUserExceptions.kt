package cat.covidcontact.server.controllers.user

import cat.covidcontact.server.controllers.CovidContactException
import org.springframework.http.HttpStatus

object ApplicationUserExceptions {
    val userExistingException = CovidContactException(
        "AU1",
        "User existing",
        HttpStatus.BAD_REQUEST
    )

    val invalidIdException = CovidContactException(
        "AU2",
        "Invalid id",
        HttpStatus.BAD_REQUEST
    )

    val userNotExistingException = CovidContactException(
        "AU3",
        "User not existing",
        HttpStatus.NOT_FOUND
    )
}