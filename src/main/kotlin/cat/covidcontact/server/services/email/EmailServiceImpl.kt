package cat.covidcontact.server.services.email

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    @Qualifier("provideJavaMailService") private val mailSender: JavaMailSenderImpl
) : EmailService {

    @Synchronized
    override fun sendConfirmationEmail(destination: String, validationCode: String) {
        val subject = "Confirm your email"
        val text = "Please go to the next link to validate your account: " +
            "http://covidcontact.cat:8080/user/validate?code=$validationCode"
        sendEmail(destination, subject, text)
    }

    private fun sendEmail(destination: String, subject: String, text: String) {
        val message = SimpleMailMessage().apply {
            setFrom(mailSender.username!!)
            setTo(destination)
            setSubject(subject)
            setText(text)
        }

        mailSender.send(message)
    }
}
