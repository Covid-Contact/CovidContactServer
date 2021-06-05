package cat.covidcontact.server.services.user

import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.user.Marriage
import cat.covidcontact.server.model.nodes.user.Occupation
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostUser
import cat.covidcontact.server.security.encrypt

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
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

    @Synchronized
    override fun makeLogOut(email: String, deviceId: String) {
        userRepository.findByEmail(email)?.let { user ->
            deviceRepository.findDeviceById(deviceId)?.let { device ->
                device.users.find { userDevice -> userDevice.user.email == email }?.isLogged = false
                deviceRepository.save(device)
            } ?: throw UserExceptions.deviceNotFound
        } ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun deleteAccount(email: String) {
        userRepository.findByEmail(email)?.let { user ->
            userRepository.delete(user)
        } ?: throw UserExceptions.userDataNotFound
    }

    override fun getAllNonOwnerMembers(contactNetworkName: String): List<User> {
        return userRepository.getAllNonOwnerMembersFromContactNetwork(contactNetworkName)
    }
}
