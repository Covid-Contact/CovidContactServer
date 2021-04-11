package cat.covidcontact.server.services.user

import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.data.user.UserRepository
import cat.covidcontact.server.post.PostUser

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val numberCalculatorService: NumberCalculatorService
) : UserService {

    override fun addUserData(user: PostUser) {
        if (userRepository.existsUserByEmail(user.email)) {
            throw UserExceptions.userDataFound
        }

        val usernameNumber = numberCalculatorService.generateRandomNumber()
        user.username = "${user.username}#$usernameNumber"

        val userNode = with(user) {
            User(
                email,
                username,
                gender,
                birthDate,
                city,
                studies,
                occupation,
                marriage,
                children,
                hasBeenPositive,
                isVaccinated
            )
        }
        userRepository.save(userNode)
    }

    override fun getUserData(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserExceptions.userDataNotFound
    }
}
