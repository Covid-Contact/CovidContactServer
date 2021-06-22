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

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostContactNetwork(
    @JsonProperty("name")
    var name: String,

    @JsonProperty("password")
    var password: String? = null,

    @JsonProperty("owner_email")
    var ownerEmail: String? = null,

    @JsonProperty("owner_username")
    var ownerUsername: String? = null,

    @JsonProperty("is_visible")
    var isVisible: Boolean = true,

    @JsonProperty("is_password_protected")
    var isPasswordProtected: Boolean = true,

    @JsonProperty("access_code")
    var accessCode: String? = null,

    @JsonProperty("state")
    var state: String? = null

) : Serializable
