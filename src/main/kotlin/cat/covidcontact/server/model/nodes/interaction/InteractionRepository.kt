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

import cat.covidcontact.server.model.nodes.user.User
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query

interface InteractionRepository : Neo4jRepository<Interaction, Long> {
    fun getInteractionsByContactNetworkName(name: String): List<Interaction>

    //@Query("match (i:Interaction)-[ui:USER_INTERACTIONS]->(u:User) return i, u")
    @Query("match (i:Interaction) where ID(i) = 15 return i")
    fun findAllInteractions(): List<Interaction>

    @Query("match (u:User) where ID(u) = 76 return u")
    fun findUsersFromInteraction(): List<User>
}
