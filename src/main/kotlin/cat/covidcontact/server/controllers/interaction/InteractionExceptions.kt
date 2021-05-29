package cat.covidcontact.server.controllers.interaction

import cat.covidcontact.server.controllers.CovidContactException
import org.springframework.http.HttpStatus

object InteractionExceptions {
    val deviceNotFound = CovidContactException(
        "I1",
        "Device not found",
        HttpStatus.NOT_FOUND
    )

    val userNotFound = CovidContactException(
        "I2",
        "User not found",
        HttpStatus.NOT_FOUND
    )
}
