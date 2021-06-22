package cat.covidcontact.server.services.email

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl

class EmailServiceImplTest {
    private lateinit var emailServiceImpl: EmailServiceImpl

    @MockK
    private lateinit var mailSender: JavaMailSenderImpl

    private val destination = "albert@gmail.com"
    private val validationCode = "1234"
    private val senderUsername = "noreplay@gmail.com"

    @BeforeEach
    fun setUp() {
        mailSender = mockk()
        emailServiceImpl = EmailServiceImpl(mailSender)
    }

    @Test
    fun `when sending confirmation email then the mail sender is used`() {
        every { mailSender.send(any<SimpleMailMessage>()) } returns Unit
        every { mailSender.username } returns senderUsername
        emailServiceImpl.sendConfirmationEmail(destination, validationCode)

        verify {
            mailSender.send(any<SimpleMailMessage>())
        }
    }
}
