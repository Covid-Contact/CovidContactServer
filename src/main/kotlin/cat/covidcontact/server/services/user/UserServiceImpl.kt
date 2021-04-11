package cat.covidcontact.server.services.user

import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.data.user.UserRepository

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val numberCalculatorService: NumberCalculatorService
) : UserService {

    override fun addUserData(user: User) {
        if (userRepository.existsUserByEmail(user.email)) {
            throw UserExceptions.userDataFound
        }

        val usernameNumber = numberCalculatorService.generateRandomNumber()
        user.username = "${user.username}#$usernameNumber"
        userRepository.save(user)
    }

    override fun getUserData(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserExceptions.userDataNotFound
    }
}
