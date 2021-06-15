package cat.covidcontact.server.services.applicationuser

import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUser

interface ApplicationUserService {
    fun createUser(applicationUser: ApplicationUser)
    fun validateUser(validateId: String)
    fun isValidated(email: String): Boolean
    fun deleteAccount(email: String)
}
