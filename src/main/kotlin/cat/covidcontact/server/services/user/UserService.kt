package cat.covidcontact.server.services.user

import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostUser

interface UserService {
    fun addUserData(user: PostUser)
    fun getUserData(email: String): User
    fun registerMessagingToken(email: String, token: String)
    fun updateUser(
        newEmail: String,
        newCity: String?,
        newStudies: String?,
        newOccupation: String?,
        newMarriage: String?,
        newChildren: Int?,
        newPositive: Boolean?,
        newVaccinated: Boolean?
    )
}
