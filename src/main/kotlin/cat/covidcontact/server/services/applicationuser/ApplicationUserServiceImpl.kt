/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cat.covidcontact.server.services.applicationuser

import cat.covidcontact.server.controllers.user.ApplicationUserExceptions
import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUser
import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUserRepository
import cat.covidcontact.server.model.authentication.verification.Verification
import cat.covidcontact.server.model.authentication.verification.VerificationRepository
import cat.covidcontact.server.services.email.EmailService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class ApplicationUserServiceImpl(
    private val emailService: EmailService,
    private val applicationUserRepository: ApplicationUserRepository,
    private val verificationRepository: VerificationRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : ApplicationUserService {
    private val codeLength = 50
    private val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    @Synchronized
    override fun createUser(applicationUser: ApplicationUser) {
        applicationUser.password = bCryptPasswordEncoder.encode(applicationUser.password)
        val userFound = applicationUserRepository.findByEmail(applicationUser.email)

        if (userFound != null) {
            throw ApplicationUserExceptions.userExistingException
        }

        val user = applicationUserRepository.save(applicationUser)
        val code = generateRandomCode()
        val verification = Verification(user.id, code)
        verificationRepository.save(verification)

        emailService.sendConfirmationEmail(applicationUser.email, code)
    }

    @Synchronized
    override fun validateUser(validateId: String) {
        val verification = verificationRepository.findByCode(validateId)
            ?: throw ApplicationUserExceptions.invalidIdException

        val applicationUser = applicationUserRepository.findById(verification.id).get()
        applicationUser.isVerified = true
        applicationUserRepository.save(applicationUser)
        verificationRepository.delete(verification)
    }

    @Synchronized
    override fun isValidated(email: String): Boolean {
        val user = findUserByEmail(email)
        return user.isVerified
    }

    @Synchronized
    override fun deleteAccount(email: String) {
        val user = findUserByEmail(email)
        applicationUserRepository.delete(user)
    }

    private fun findUserByEmail(email: String): ApplicationUser {
        return applicationUserRepository.findByEmail(email)
            ?: throw ApplicationUserExceptions.userNotExistingException
    }

    private fun generateRandomCode() = List(codeLength) { charset.random() }.joinToString("")
}
