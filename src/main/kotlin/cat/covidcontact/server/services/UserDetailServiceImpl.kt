package cat.covidcontact.server.services

import cat.covidcontact.server.data.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        println(email)
        val applicationUser = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException(email)

        println("HERE")

        return User(applicationUser.email, applicationUser.password, emptyList())
    }
}