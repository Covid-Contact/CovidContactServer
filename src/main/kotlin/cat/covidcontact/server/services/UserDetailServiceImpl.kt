package cat.covidcontact.server.services

import cat.covidcontact.server.data.applicationuser.ApplicationUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailServiceImpl(
    private val applicationUserRepository: ApplicationUserRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        println(email)
        val applicationUser = applicationUserRepository.findByEmail(email)
            ?: throw UsernameNotFoundException(email)

        return User(applicationUser.email, applicationUser.password, emptyList())
    }
}