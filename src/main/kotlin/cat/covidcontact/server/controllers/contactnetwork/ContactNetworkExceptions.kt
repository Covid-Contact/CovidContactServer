/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
