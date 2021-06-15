package cat.covidcontact.server.model.nodes.location

import cat.covidcontact.server.model.nodes.interaction.Interaction
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
data class City(
    @Id
    var name: String,

    @Relationship(type = "TAKE_PLACE", direction = Relationship.Direction.OUTGOING)
    var interactions: MutableSet<Interaction> = mutableSetOf()
)
