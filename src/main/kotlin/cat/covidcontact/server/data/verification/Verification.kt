package cat.covidcontact.server.data.verification

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "verification")
@Suppress("JpaDataSourceORMInspection")
data class Verification(
    @Id
    val id: Long,
    val code: String
)
