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

    val contactNetworkNotExisting = CovidContactException(
        "CN4",
        "The contact network does not exist",
        HttpStatus.NOT_FOUND
    )

    val invalidAccessCode = CovidContactException(
        "CN5",
        "The access code is invalid,",
        HttpStatus.NOT_FOUND
    )

    val userAlreadyJoined = CovidContactException(
        "CN6",
        "The user has already joined this contact network",
        HttpStatus.BAD_REQUEST
    )

    val userIsNotOwner = CovidContactException(
        "CN7",
        "The user is not the owner",
        HttpStatus.BAD_REQUEST
    )
}
