package cat.covidcontact.server.data.userdevice

import cat.covidcontact.server.data.device.Device
import cat.covidcontact.server.data.user.User
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node
class UserDevice(
    @Relationship(type = "IS_OWNED_BY", direction = Relationship.Direction.OUTGOING)
    val user: User,

    @Relationship(type = "IS_USING", direction = Relationship.Direction.OUTGOING)
    val device: Device,

    var isLogged: Boolean = false
) {
    @Id
    @GeneratedValue
    var id: Long? = null
}
