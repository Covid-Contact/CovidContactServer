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

object ContactNetworkControllerUrls {
    const val BASE = "/contactnetwork"
    const val CREATE_CONTACT_NETWORK = "/"
    const val GET_CONTACT_NETWORKS = "/"
    const val ENABLE_USER_ADDITION = "/{name}"
    const val GENERATE_ACCESS_CODE = "/{name}/generateAccessCode"
    const val GET_CONTACT_NETWORK_BY_ACCESS_CODE = "/accessCode"
    const val JOIN_CONTACT_NETWORK = "/{name}/join"
    const val EXIT_CONTACT_NETWORK = "/{name}/exit"
    const val DELETE_CONTACT_NETWORK = "/{name}/delete"
    const val UPDATE_CONTACT_NETWORK_VISIBILITY = "/{name}/visibility"
    const val UPDATE_CONTACT_NETWORK_PASSWORD = "/{name}/pasword"
    const val UPDATE_CONTACT_NETWORK_IS_PASSWORD_PROTECTED = "/{name}/protected"
    const val GET_CONTACT_NETWORK_IF_NOT_MEMBER = "/{name}/nomember"
    const val GET_ALL_NON_OWNER_MEMBERS = "/{name}/noownermembers"
    const val DELETE_MEMBER = "/{name}/member"
}
