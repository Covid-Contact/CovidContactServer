package cat.covidcontact.server.services.applicationuser

import cat.covidcontact.server.controllers.user.ApplicationUserExceptions
import cat.covidcontact.server.data.applicationuser.ApplicationUser
import cat.covidcontact.server.data.applicationuser.ApplicationUserRepository
import cat.covidcontact.server.data.verification.Verification
import cat.covidcontact.server.data.verification.VerificationRepository
import cat.covidcontact.server.services.email.EmailService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class ApplicationUserServiceImpl(
    private val emailService: EmailService,
    private val applicationUserRepository: ApplicationUserRepository,
    private val verificationRepository: VerificationRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : ApplicationUserService {

    private val codeLength = 50
    private val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun createUser(applicationUser: ApplicationUser) {
        applicationUser.password = bCryptPasswordEncoder.encode(applicationUser.password)
        val userFound = applicationUserRepository.findByEmail(applicationUser.email)

        if (userFound != null) {
            throw ApplicationUserExceptions.userExistingException
        }

        val user = applicationUserRepository.save(applicationUser)
        val code = generateRandomCode()
        val verification = Verification(user.id, code)
        verificationRepository.save(verification)

        emailService.sendConfirmationEmail(applicationUser.email, code)
    }

    override fun validateUser(validateId: String) {
        val verification = verificationRepository.findByCode(validateId)
            ?: throw ApplicationUserExceptions.invalidIdException

        val applicationUser = applicationUserRepository.findById(verification.id).get()
        applicationUser.isVerified = true
        applicationUserRepository.save(applicationUser)
        verificationRepository.delete(verification)
    }

    override fun isValidated(email: String): Boolean {
        val user = applicationUserRepository.findByEmail(email)
            ?: throw ApplicationUserExceptions.userNotExistingException

        return user.isVerified
    }

    private fun generateRandomCode() = List(codeLength) { charset.random() }.joinToString("")
}
