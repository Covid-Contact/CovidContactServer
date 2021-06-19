package cat.covidcontact.server.services

import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUserRepository
import cat.covidcontact.server.model.authentication.verification.VerificationRepository
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.location.CountryRepository
import cat.covidcontact.server.model.nodes.location.ProvinceRepository
import cat.covidcontact.server.model.nodes.location.RegionRepository
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
import cat.covidcontact.server.services.location.LocationService
import cat.covidcontact.server.services.location.LocationServiceImpl
import cat.covidcontact.server.services.messaging.MessagingService
import cat.covidcontact.server.services.messaging.MessagingServiceImpl
import cat.covidcontact.server.services.statistics.StatisticsService
import cat.covidcontact.server.services.statistics.StatisticsServiceImpl
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
        bCryptPasswordEncoder: BCryptPasswordEncoder
    ): ApplicationUserService = ApplicationUserServiceImpl(
        emailService,
        applicationUserRepository,
        verificationRepository,
        bCryptPasswordEncoder
    )

    @Bean
    fun provideUserService(
        userRepository: UserRepository,
        deviceRepository: DeviceRepository,
        countryRepository: CountryRepository,
        numberCalculatorService: NumberCalculatorService,
        locationService: LocationService
    ): UserService = UserServiceImpl(
        userRepository,
        deviceRepository,
        countryRepository,
        numberCalculatorService,
        locationService
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
    ): ContactNetworkService = ContactNetworkServiceImpl(
        contactNetworkRepository,
        userRepository,
        numberCalculatorService
    )

    @Bean
    fun provideInteractionService(
        deviceRepository: DeviceRepository,
        interactionRepository: InteractionRepository,
        userRepository: UserRepository,
        contactNetworkRepository: ContactNetworkRepository,
        countryRepository: CountryRepository,
        locationService: LocationService,
        messagingService: MessagingService
    ): InteractionService = InteractionServiceImpl(
        deviceRepository,
        interactionRepository,
        userRepository,
        contactNetworkRepository,
        countryRepository,
        locationService,
        messagingService
    )

    @Bean
    fun provideLocationService(): LocationService = LocationServiceImpl()

    @Bean
    fun provideMessagingService(
        firebaseMessaging: FirebaseMessaging
    ): MessagingService = MessagingServiceImpl(
        firebaseMessaging
    )

    @Bean
    fun provideStatisticsService(
        interactionRepository: InteractionRepository,
        countryRepository: CountryRepository,
        regionRepository: RegionRepository,
        provinceRepository: ProvinceRepository
    ): StatisticsService = StatisticsServiceImpl(
        interactionRepository,
        countryRepository,
        regionRepository,
        provinceRepository
    )
}
