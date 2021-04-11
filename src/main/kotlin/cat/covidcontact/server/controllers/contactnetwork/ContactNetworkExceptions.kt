package cat.covidcontact.server.controllers.contactnetwork

import cat.covidcontact.server.controllers.CovidContactException
import org.springframework.http.HttpStatus

object ContactNetworkExceptions {
    val ownerEmailNotFound = CovidContactException(
        "CN1",
        "Owner email not found",
        HttpStatus.BAD_REQUEST
    )

    val contactNetworkFoundForUser = CovidContactException(
        "CN2",
        "The user already has a contact net with that name",
        HttpStatus.BAD_REQUEST
    )

    val emailNotFound = CovidContactException(
        "CN3",
        "The email is not found",
        HttpStatus.NOT_FOUND
    )
}
