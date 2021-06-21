package cat.covidcontact.server.services.applicationuser

import cat.covidcontact.server.controllers.CovidContactException
import cat.covidcontact.server.controllers.user.ApplicationUserExceptions
import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUser
import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUserRepository
import cat.covidcontact.server.model.authentication.verification.Verification
import cat.covidcontact.server.model.authentication.verification.VerificationRepository
import cat.covidcontact.server.services.email.EmailService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

class ApplicationUserServiceImplTest {
    private lateinit var applicationUserServiceImpl: ApplicationUserServiceImpl

    @MockK
    private lateinit var emailService: EmailService

    @MockK
    private lateinit var applicationUserRepository: ApplicationUserRepository

    @MockK
    private lateinit var verificationRepository: VerificationRepository

    @MockK
    private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @MockK
    private lateinit var optional: Optional<ApplicationUser>

    private val applicationUser = ApplicationUser(
        id = 0,
        email = "albert@gmail.com",
        password = "1234",
        isVerified = false
    )

    private val verification = Verification(
        id = 0,
        code = "1234"
    )

    @BeforeEach
    fun setUp() {
        emailService = mockk()
        applicationUserRepository = mockk()
        verificationRepository = mockk()
        bCryptPasswordEncoder = mockk()
        every { bCryptPasswordEncoder.encode(any()) } returns "Encrypted"

        applicationUserServiceImpl = ApplicationUserServiceImpl(
            emailService,
            applicationUserRepository,
            verificationRepository,
            bCryptPasswordEncoder
        )

        optional = mockk()
    }

    @Test
    fun `when user is found then exception is thrown`() {
        every {
            applicationUserRepository.findByEmail(any())
        } throws ApplicationUserExceptions.userExistingException

        assertThrows<CovidContactException> {
            applicationUserServiceImpl.createUser(applicationUser)
        }
    }

    @Test
    fun `when user is not found then it is created`() {
        every { applicationUserRepository.findByEmail(any()) } returns null
        every { applicationUserRepository.save(any()) } returns applicationUser
        every { verificationRepository.save(any()) } returns verification
        every { emailService.sendConfirmationEmail(any(), any()) } returns Unit

        applicationUserServiceImpl.createUser(applicationUser)

        verify {
            applicationUserRepository.findByEmail(any())
            applicationUserRepository.save(any())
            verificationRepository.save(any())
            emailService.sendConfirmationEmail(any(), any())
        }
    }

    @Test
    fun `when validating user the verification id is not found then exception is thrown`() {
        every {
            verificationRepository.findByCode(any())
        } throws ApplicationUserExceptions.invalidIdException

        assertThrows<CovidContactException> {
            applicationUserServiceImpl.validateUser("1")
        }
    }

    @Test
    fun `when validating user the verification id is not found then user is validated`() {
        every { verificationRepository.findByCode(any()) } returns verification
        every { applicationUserRepository.findById(any()) } returns optional
        every { optional.get() } returns applicationUser
        every { applicationUserRepository.save(any()) } returns applicationUser
        every { verificationRepository.delete(any()) } returns Unit

        applicationUserServiceImpl.validateUser("1")

        verify {
            verificationRepository.findByCode(any())
            applicationUserRepository.findById(any())
            applicationUserRepository.save(any())
            verificationRepository.delete(any())
        }
    }

    @Test
    fun `when checking if user is validated it does not exist then exception is thrown`() {
        every {
            applicationUserRepository.findByEmail(any())
        } throws ApplicationUserExceptions.userNotExistingException

        assertThrows<CovidContactException> {
            applicationUserServiceImpl.isValidated(applicationUser.email)
        }
    }

    @Test
    fun `when deleting user it does not exist then exception is thrown`() {
        every {
            applicationUserRepository.findByEmail(any())
        } throws ApplicationUserExceptions.userNotExistingException

        assertThrows<CovidContactException> {
            applicationUserServiceImpl.deleteAccount(applicationUser.email)
        }
    }

    @Test
    fun `when deleting user it exists then it is deleted`() {
        every {
            applicationUserRepository.findByEmail(any())
        } returns applicationUser
        every { applicationUserRepository.delete(any()) } returns Unit

        applicationUserServiceImpl.deleteAccount(applicationUser.email)

        verify {
            applicationUserRepository.findByEmail(any())
            applicationUserRepository.delete(any())
        }
    }
}
