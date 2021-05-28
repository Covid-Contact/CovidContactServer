package cat.covidcontact.server.services.user

import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.model.nodes.user.Marriage
import cat.covidcontact.server.model.nodes.user.Occupation
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostUser

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
    override fun updateUser(
        newEmail: String,
        newCity: String?,
        newStudies: String?,
        newOccupation: String?,
        newMarriage: String?,
        newChildren: Int?,
        newPositive: Boolean?,
        newVaccinated: Boolean?
    ) {
        userRepository.findByEmail(newEmail)?.let { user ->
            val newUser = user.apply {
                city = newCity ?: city
                studies = newStudies ?: studies
                occupation = newOccupation?.let { occupation -> Occupation.valueOf(occupation) }
                    ?: occupation
                marriage = newMarriage?.let { marriage -> Marriage.valueOf(marriage) } ?: marriage
                children = newChildren ?: children
                hasBeenPositive = newPositive ?: hasBeenPositive
                isVaccinated = newVaccinated ?: isVaccinated
            }

            userRepository.save(newUser)
        } ?: UserExceptions.userDataNotFound
    }
}
