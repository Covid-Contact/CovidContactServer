package cat.covidcontact.server.model.nodes.user

import cat.covidcontact.server.model.nodes.member.Member
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import java.io.Serializable

@Node
data class User(
    @Id
    val email: String,
    var username: String,
    val gender: Gender,
    val birthDate: Long,
    var city: String? = null,
    var studies: String? = null,
    var occupation: Occupation? = null,
    var marriage: Marriage? = null,
    var children: Int? = null,
    var hasBeenPositive: Boolean? = null,
    var isVaccinated: Boolean? = null,

    @Relationship(type = "MEMBER", direction = Relationship.Direction.OUTGOING)
    var contactNetworks: MutableList<Member> = mutableListOf()
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}