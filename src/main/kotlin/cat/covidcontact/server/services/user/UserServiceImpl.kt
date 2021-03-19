package cat.covidcontact.server.services.user

import cat.covidcontact.server.controllers.UserException
import cat.covidcontact.server.data.applicationuser.ApplicationUser
import cat.covidcontact.server.data.applicationuser.ApplicationUserRepository
import cat.covidcontact.server.data.verification.Verification
import cat.covidcontact.server.data.verification.VerificationRepository
import cat.covidcontact.server.services.email.EmailService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.random.Random

class UserServiceImpl(
    private val applicationUserRepository: ApplicationUserRepository,
    private val emailService: EmailService,
    private val verificationRepository: VerificationRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : UserService {

    override fun createUser(applicationUser: ApplicationUser) {
        applicationUser.password = bCryptPasswordEncoder.encode(applicationUser.password)
        val userFound = applicationUserRepository.findByEmail(applicationUser.email)

        if (userFound != null) {
            throw UserException.UserExisting()
        }

        val user = applicationUserRepository.save(applicationUser)
        val code = (Random.nextBits(32).toString() + user.email).hashCode()
        val verification = Verification(user.id, code.toString())
        verificationRepository.save(verification)

        emailService.sendConfirmationEmail(applicationUser.email, code)
    }

    override fun validateUser(validateId: String) {
        val verification = verificationRepository.findByCode(validateId)
            ?: throw UserException.InvalidId()

        val applicationUser = applicationUserRepository.findById(verification.id).get()
        applicationUser.isVerified = true
        applicationUserRepository.save(applicationUser)
        verificationRepository.delete(verification)
    }
}
