package cat.covidcontact.server.services.user

import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.post.PostUser

interface UserService {
    fun addUserData(user: PostUser)

    fun getUserData(email: String): User
}