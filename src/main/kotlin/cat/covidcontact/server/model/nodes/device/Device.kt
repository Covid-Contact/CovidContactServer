package cat.covidcontact.server.model.nodes.device

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
data class Device(
    @Id
    var id: String,
    var name: String,

    @Relationship(type = "IS_USED_BY", direction = Relationship.Direction.OUTGOING)
    var users: MutableList<UserDevice> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Device

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
