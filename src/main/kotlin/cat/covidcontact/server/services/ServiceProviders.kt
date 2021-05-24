package cat.covidcontact.server.services

import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUserRepository
import cat.covidcontact.server.model.authentication.message.MessageRepository
import cat.covidcontact.server.model.authentication.verification.VerificationRepository
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.services.applicationuser.ApplicationUserService
import cat.covidcontact.server.services.applicationuser.ApplicationUserServiceImpl
import cat.covidcontact.server.services.contactnetwork.ContactNetworkService
import cat.covidcontact.server.services.contactnetwork.ContactNetworkServiceImpl
import cat.covidcontact.server.services.device.DeviceService
import cat.covidcontact.server.services.device.DeviceServiceImpl
import cat.covidcontact.server.services.email.EmailService
import cat.covidcontact.server.services.email.EmailServiceImpl
import cat.covidcontact.server.services.interaction.InteractionService
import cat.covidcontact.server.services.interaction.InteractionServiceImpl
import cat.covidcontact.server.services.user.NumberCalculatorService
import cat.covidcontact.server.services.user.NumberCalculatorServiceImpl
import cat.covidcontact.server.services.user.UserService
import cat.covidcontact.server.services.user.UserServiceImpl
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class ServiceProviders {

    @Bean
    fun provideApplicationUserService(
        emailService: EmailService,
        applicationUserRepository: ApplicationUserRepository,
        verificationRepository: VerificationRepository,
        messageRepository: MessageRepository,
        bCryptPasswordEncoder: BCryptPasswordEncoder
    ): ApplicationUserService = ApplicationUserServiceImpl(
        emailService,
        applicationUserRepository,
        verificationRepository,
        messageRepository,
        bCryptPasswordEncoder
    )

    @Bean
    fun provideUserService(
        userRepository: UserRepository,
        numberCalculatorService: NumberCalculatorService
    ): UserService = UserServiceImpl(
        userRepository, numberCalculatorService
    )

    @Bean
    fun provideNumberCalculatorService(): NumberCalculatorService = NumberCalculatorServiceImpl()

    @Bean
    fun provideEmailService(
        @Qualifier("provideJavaMailService") javaMailSenderImpl: JavaMailSenderImpl
    ): EmailService = EmailServiceImpl(javaMailSenderImpl)

    @Bean
    fun provideJavaMailService(): JavaMailSenderImpl = JavaMailSenderImpl().apply {
        host = "smtp.gmail.com"
        port = 587
        username = "noreply.covidcontact@gmail.com"
        password = "Europe2021%"
        javaMailProperties.let { props ->
            props["mail.transport.protocol"] = "smtp"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.debug"] = "true"
        }
    }

    @Bean
    fun provideDeviceService(
        deviceRepository: DeviceRepository
    ): DeviceService = DeviceServiceImpl(deviceRepository)

    @Bean
    fun provideContactNetworkService(
        contactNetworkRepository: ContactNetworkRepository,
        userRepository: UserRepository,
        numberCalculatorService: NumberCalculatorService,
        firebaseMessaging: FirebaseMessaging
    ): ContactNetworkService = ContactNetworkServiceImpl(
        contactNetworkRepository,
        userRepository,
        numberCalculatorService,
        firebaseMessaging
    )

    @Bean
    fun provideInteractionService(
        deviceRepository: DeviceRepository,
        interactionRepository: InteractionRepository
    ): InteractionService = InteractionServiceImpl(
        deviceRepository,
        interactionRepository
    )
}
