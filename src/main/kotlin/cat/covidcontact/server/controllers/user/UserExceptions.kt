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

    val deviceNotFound = CovidContactException(
        "U3",
        "Device not found",
        HttpStatus.NOT_FOUND
    )
}
