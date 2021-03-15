package cat.covidcontact.server.services

import cat.covidcontact.server.data.UserRepository
import cat.covidcontact.server.services.user.UserService
import cat.covidcontact.server.services.user.UserServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class ServiceProviders {

    @Bean
    fun provideUserService(
        userRepository: UserRepository,
        bCryptPasswordEncoder: BCryptPasswordEncoder
    ): UserService = UserServiceImpl(userRepository, bCryptPasswordEncoder)
}