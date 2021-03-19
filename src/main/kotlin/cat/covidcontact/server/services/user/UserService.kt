package cat.covidcontact.server.services.user

import cat.covidcontact.server.data.applicationuser.ApplicationUser

interface UserService {
    fun createUser(applicationUser: ApplicationUser)

    fun validateUser(validateId: String)
}