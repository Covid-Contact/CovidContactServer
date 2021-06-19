package cat.covidcontact.server.model.nodes.interaction

import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.location.City
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
data class Interaction(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var startDateTime: Long,
    var endDateTime: Long? = null,
    var duration: Long? = null,
    var isDangerous: Boolean? = null,
    var userInteractions: MutableList<UserInteraction> = mutableListOf(),

    @Relationship(type = "TAKE_PLACE_IN", direction = Relationship.Direction.OUTGOING)
    var contactNetwork: ContactNetwork? = null,

    @Relationship(type = "TAKE_PLACE", direction = Relationship.Direction.INCOMING)
    var city: City? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Interaction

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}
