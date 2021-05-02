package cat.covidcontact.server.data.device

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class DeviceUser(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @TargetNode
    var device: Device
)
