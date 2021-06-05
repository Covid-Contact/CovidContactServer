package cat.covidcontact.server.services.applicationuser

import cat.covidcontact.server.controllers.user.ApplicationUserExceptions
import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUser
import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUserRepository
import cat.covidcontact.server.model.authentication.verification.Verification
import cat.covidcontact.server.model.authentication.verification.VerificationRepository
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

    @Synchronized
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

    @Synchronized
    override fun validateUser(validateId: String) {
        val verification = verificationRepository.findByCode(validateId)
            ?: throw ApplicationUserExceptions.invalidIdException

        val applicationUser = applicationUserRepository.findById(verification.id).get()
        applicationUser.isVerified = true
        applicationUserRepository.save(applicationUser)
        verificationRepository.delete(verification)
    }

    @Synchronized
    override fun isValidated(email: String): Boolean {
        val user = findUserByEmail(email)
        return user.isVerified
    }

    @Synchronized
    override fun deleteAccount(email: String) {
        val user = findUserByEmail(email)
        applicationUserRepository.delete(user)
    }

    private fun findUserByEmail(email: String): ApplicationUser {
        return applicationUserRepository.findByEmail(email)
            ?: throw ApplicationUserExceptions.userNotExistingException
    }

    private fun generateRandomCode() = List(codeLength) { charset.random() }.joinToString("")
}
