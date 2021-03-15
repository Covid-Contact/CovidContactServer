package cat.covidcontact.server.data

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<ApplicationUser, Long> {
    fun findByEmail(email: String): ApplicationUser?
}