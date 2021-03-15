package cat.covidcontact.server.services.user

import cat.covidcontact.server.data.ApplicationUser
import cat.covidcontact.server.data.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : UserService {
    override fun createUser(user: ApplicationUser) {
        user.password = bCryptPasswordEncoder.encode(user.password)
        userRepository.save(user)
    }
}
