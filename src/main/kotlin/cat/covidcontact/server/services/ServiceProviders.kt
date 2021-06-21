package cat.covidcontact.server.services

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class ServiceProviders {

    /*@Bean
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
    ): EmailService = EmailServiceImpl(javaMailSenderImpl)*/

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

    /*@Bean
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
    )*/
}
