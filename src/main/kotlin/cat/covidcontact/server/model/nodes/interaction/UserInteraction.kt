package cat.covidcontact.server.model.nodes.interaction

import cat.covidcontact.server.model.nodes.user.User
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class UserInteraction(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var isEnded: Boolean = false,

    @TargetNode
    var user: User
)
