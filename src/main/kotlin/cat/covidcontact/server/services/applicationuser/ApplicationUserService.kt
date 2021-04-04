package cat.covidcontact.server.services.applicationuser

import cat.covidcontact.server.data.applicationuser.ApplicationUser

interface ApplicationUserService {
    fun createUser(applicationUser: ApplicationUser)

    fun validateUser(validateId: String)

    fun isValidated(email: String): Boolean
}
