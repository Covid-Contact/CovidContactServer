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

package cat.covidcontact.server.model.post

import cat.covidcontact.server.model.nodes.user.Gender
import cat.covidcontact.server.model.nodes.user.Marriage
import cat.covidcontact.server.model.nodes.user.Occupation
import cat.covidcontact.server.model.nodes.user.UserState
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostUser(
    @JsonProperty("email")
    val email: String,

    @JsonProperty("username")
    var username: String,

    @JsonProperty("gender")
    val gender: Gender,

    @JsonProperty("birth_date")
    val birthDate: Long,

    @JsonProperty("city")
    var city: String? = null,

    @JsonProperty("studies")
    var studies: String? = null,

    @JsonProperty("occupation")
    var occupation: Occupation? = null,

    @JsonProperty("marriage")
    var marriage: Marriage? = null,

    @JsonProperty("children")
    var children: Int? = null,

    @JsonProperty("has_been_positive")
    var hasBeenPositive: Boolean? = null,

    @JsonProperty("is_vaccinated")
    var isVaccinated: Boolean? = null,

    @JsonProperty("state")
    var state: UserState? = null
) : Serializable
