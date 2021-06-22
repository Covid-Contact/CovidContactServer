package cat.covidcontact.server.services

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class ServiceProviders {
    private val noReplyPassword = System.getenv("COVID_CONTACT_NO_REPLY")

    @Bean
    fun provideJavaMailService(): JavaMailSenderImpl = JavaMailSenderImpl().apply {
        host = "smtp.gmail.com"
        port = 587
        username = "noreply.covidcontact@gmail.com"
        password = noReplyPassword
        javaMailProperties.let { props ->
            props["mail.transport.protocol"] = "smtp"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.debug"] = "true"
        }
    }
}
