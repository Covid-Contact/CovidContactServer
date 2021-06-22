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

package cat.covidcontact.server.model.nodes.interaction

import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.location.City
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
data class Interaction(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var startDateTime: Long,
    var endDateTime: Long? = null,
    var duration: Long? = null,
    var isDangerous: Boolean? = null,
    var userInteractions: MutableList<UserInteraction> = mutableListOf(),

    @Relationship(type = "TAKE_PLACE_IN", direction = Relationship.Direction.OUTGOING)
    var contactNetwork: ContactNetwork? = null,

    @Relationship(type = "TAKE_PLACE", direction = Relationship.Direction.INCOMING)
    var cities: MutableSet<City> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Interaction

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}
