package cat.covidcontact.server.model.nodes.member

import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class Member(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var isOwner: Boolean = false,

    @TargetNode
    var contactNetwork: ContactNetwork
)
