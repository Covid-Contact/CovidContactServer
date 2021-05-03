package cat.covidcontact.server.model.authentication.applicationuser

import org.springframework.data.jpa.repository.JpaRepository

interface ApplicationUserRepository : JpaRepository<ApplicationUser, Long> {
    fun findByEmail(email: String): ApplicationUser?
}
