package cat.covidcontact.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class Configuration {

    @Bean
    fun provideBCryptPasswordEncoder() = BCryptPasswordEncoder()
}