package cat.covidcontact.server.services.user

import cat.covidcontact.server.data.applicationuser.ApplicationUser
import cat.covidcontact.server.data.user.User

interface UserService {
    fun createUser(applicationUser: ApplicationUser)

    fun validateUser(validateId: String)

    fun isValidated(email: String): Boolean

    fun addUserInfo(user: User)
}
