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

package cat.covidcontact.server.model.nodes.user

import cat.covidcontact.server.model.nodes.location.City
import cat.covidcontact.server.model.nodes.member.Member
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import java.io.Serializable

@Node
data class User(
    @Id
    val email: String = "",
    var username: String = "",
    val gender: Gender = Gender.Other,
    val birthDate: Long = 0,
    var studies: String? = null,
    var occupation: Occupation? = null,
    var marriage: Marriage? = null,
    var children: Int? = null,
    var hasBeenPositive: Boolean? = null,
    var isVaccinated: Boolean? = null,
    var state: UserState = UserState.Normal,
    var messagingToken: String? = null,

    @Relationship(type = "MEMBER", direction = Relationship.Direction.OUTGOING)
    var contactNetworks: MutableList<Member> = mutableListOf(),

    @Relationship(type = "LIVES_IN", direction = Relationship.Direction.OUTGOING)
    var city: City? = null,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}
