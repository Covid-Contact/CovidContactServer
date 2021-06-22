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

package cat.covidcontact.server.model.nodes.contactnetwork

import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query


interface ContactNetworkRepository : Neo4jRepository<ContactNetwork, String> {
    fun findContactNetworkByName(name: String): ContactNetwork?
    fun existsContactNetworkByAccessCode(accessCode: String): Boolean
    fun findContactNetworkByAccessCode(accessCode: String): ContactNetwork?

    @Query("match (c:ContactNetwork{name: \$name}) set c.state = 'Deleted'")
    fun setStateToDeleted(name: String)
}
