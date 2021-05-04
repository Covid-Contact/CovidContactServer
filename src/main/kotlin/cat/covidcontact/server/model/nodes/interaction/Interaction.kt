package cat.covidcontact.server.model.nodes.interaction

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node
data class Interaction(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var startDateTime: Long,
    var endDateTime: Long? = null,
    var userInteractions: MutableList<UserInteraction> = mutableListOf()
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
