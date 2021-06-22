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

package cat.covidcontact.server.services.user

import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostUser

interface UserService {
    fun addUserData(user: PostUser)
    fun getUserData(email: String): User
    fun registerMessagingToken(email: String, token: String)
    fun updateUser(
        newEmail: String,
        newCity: String?,
        newStudies: String?,
        newOccupation: String?,
        newMarriage: String?,
        newChildren: Int?,
        newPositive: Boolean?,
        newVaccinated: Boolean?
    )

    fun makeLogOut(email: String, deviceId: String)
    fun deleteAccount(email: String)
    fun getAllNonOwnerMembers(contactNetworkName: String): List<User>
}
