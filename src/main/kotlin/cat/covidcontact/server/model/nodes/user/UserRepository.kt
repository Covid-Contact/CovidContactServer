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

import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param

interface UserRepository : Neo4jRepository<User, String> {

    fun findByEmail(email: String): User?

    fun existsUserByEmail(email: String): Boolean

    @Query("match (u:User {email: \$email})-[m:MEMBER]->(c:ContactNetwork {name: \$name}) detach delete m")
    fun removeMember(@Param("email") email: String, @Param("name") contactNetworkName: String)

    @Query("match (u:User)-[:MEMBER]->(c:ContactNetwork{name: \$contactNetworkName}) return u")
    fun getAllMembersFromContactNetwork(contactNetworkName: String): List<User>

    @Query("match (u:User)-[:MEMBER{isOwner: false}]->(c:ContactNetwork{name: \$contactNetworkName}}) return u")
    fun getAllNonOwnerMembersFromContactNetwork(contactNetworkName: String): List<User>
}
