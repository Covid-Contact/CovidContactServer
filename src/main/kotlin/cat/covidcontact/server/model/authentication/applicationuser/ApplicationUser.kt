package cat.covidcontact.server.model.authentication.applicationuser

import javax.persistence.*

@Entity
@Table(name = "users")
@Suppress("JpaDataSourceORMInspection")
data class ApplicationUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long,
    var email: String,
    var password: String,

    @Column(name = "is_verified")
    var isVerified: Boolean = false
)
