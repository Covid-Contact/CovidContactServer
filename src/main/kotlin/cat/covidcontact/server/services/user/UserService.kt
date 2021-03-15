package cat.covidcontact.server.services.user

import cat.covidcontact.server.data.ApplicationUser

interface UserService {
    fun createUser(user: ApplicationUser)
}