package cat.covidcontact.server.model.nodes.device

import cat.covidcontact.server.model.nodes.user.User
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class UserDevice(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var isLogged: Boolean = false,

    @TargetNode
    var user: User
)
