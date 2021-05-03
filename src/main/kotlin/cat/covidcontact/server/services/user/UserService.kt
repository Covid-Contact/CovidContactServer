package cat.covidcontact.server.services.user

import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostUser

interface UserService {
    fun addUserData(user: PostUser)

    fun getUserData(email: String): User
}