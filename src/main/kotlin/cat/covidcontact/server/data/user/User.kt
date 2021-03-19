package cat.covidcontact.server.data.user

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node
class User(@Id val email: String) {
    val username = email
    val gender = "Male"
    val studies: String? = null
    val occupation: String? = null
    val marriage: String? = null
    val children: Int? = null
}