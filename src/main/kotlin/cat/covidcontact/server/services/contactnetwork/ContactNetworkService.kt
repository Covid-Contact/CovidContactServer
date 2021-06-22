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

package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.post.PostContactNetwork

interface ContactNetworkService {
    fun createContactNetwork(postContactNetwork: PostContactNetwork): ContactNetwork
    fun getContactNetworksFromUser(email: String): List<ContactNetwork>
    fun enableUserAddition(contactNetworkName: String, isEnabled: Boolean)
    fun generateAccessCode(contactNetworkName: String): String
    fun getContactNetworkByAccessCode(accessCode: String): ContactNetwork
    fun joinContactNetwork(contactNetworkName: String, email: String)
    fun exitContactNetwork(contactNetworkName: String, email: String)
    fun deleteContactNetwork(name: String, email: String)
    fun updateVisibility(name: String, email: String, isVisible: Boolean)
    fun updatePassword(name: String, password: String, email: String)
    fun updateIsPasswordProtected(name: String, isProtected: Boolean, email: String)
    fun getContactNetworkIfNotMember(
        contactNetworkName: String,
        email: String
    ): List<ContactNetwork>

    fun deleteMember(contactNetworkName: String, memberEmail: String, email: String)
}
