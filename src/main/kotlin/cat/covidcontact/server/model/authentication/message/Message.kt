package cat.covidcontact.server.model.authentication.message

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "messaging")
@Suppress("JpaDataSourceORMInspection")
data class Message(
    @Id
    val id: Long,
    val token: String
)
