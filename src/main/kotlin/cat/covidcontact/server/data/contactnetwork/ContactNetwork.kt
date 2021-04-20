package cat.covidcontact.server.data.contactnetwork

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node
data class ContactNetwork(
    @Id
    var name: String,
    var password: String? = null,
    var ownerUsername: String? = null,
    var isVisible: Boolean = true,
    var isPasswordProtected: Boolean = true
)
