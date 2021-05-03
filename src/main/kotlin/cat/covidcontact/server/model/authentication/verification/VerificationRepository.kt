package cat.covidcontact.server.model.authentication.verification

import org.springframework.data.jpa.repository.JpaRepository

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun findByCode(code: String): Verification?
}
