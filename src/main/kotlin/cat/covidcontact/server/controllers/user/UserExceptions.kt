package cat.covidcontact.server.controllers.user

import cat.covidcontact.server.controllers.CovidContactException
import org.springframework.http.HttpStatus

object UserExceptions {
    val userDataNotFound = CovidContactException(
        "U1",
        "User data not found",
        HttpStatus.NOT_FOUND
    )

    val userDataFound = CovidContactException(
        "U2",
        "User data found",
        HttpStatus.BAD_REQUEST
    )
}