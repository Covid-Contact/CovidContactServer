package cat.covidcontact.server.data.verification

import org.springframework.data.jpa.repository.JpaRepository

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun findByCode(code: String): Verification?
}
