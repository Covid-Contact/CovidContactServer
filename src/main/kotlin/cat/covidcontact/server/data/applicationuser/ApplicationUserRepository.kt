package cat.covidcontact.server.data.applicationuser

import org.springframework.data.jpa.repository.JpaRepository

interface ApplicationUserRepository : JpaRepository<ApplicationUser, Long> {
    fun findByEmail(email: String): ApplicationUser?
}
