package cat.covidcontact.server.services.user

import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostUser
import cat.covidcontact.server.security.encrypt

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val numberCalculatorService: NumberCalculatorService
) : UserService {

    @Synchronized
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

    @Synchronized
    override fun getUserData(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun registerMessagingToken(email: String, token: String) {
        userRepository.findByEmail(email)?.let { user ->
            user.messagingToken = token.encrypt()
            userRepository.save(user)
        }
    }
}
