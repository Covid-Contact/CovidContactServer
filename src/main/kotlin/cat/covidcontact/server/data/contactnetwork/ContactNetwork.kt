package cat.covidcontact.server.data.contactnetwork

import cat.covidcontact.server.data.user.User
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
data class ContactNetwork(
    @Id
    var name: String,
    var password: String? = null,

    @Relationship(type = "OWNER", direction = Relationship.Direction.OUTGOING)
    var owner: User,

    @Relationship(type = "MEMBER", direction = Relationship.Direction.INCOMING)
    var members: List<User>
)
