package cat.covidcontact.server.services.user

import cat.covidcontact.server.data.user.User

interface UserService {
    fun addUserData(user: User)

    fun getUserData(email: String): User
}