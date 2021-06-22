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

package cat.covidcontact.server.services.email

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    @Qualifier("provideJavaMailService") private val mailSender: JavaMailSenderImpl
) : EmailService {

    @Synchronized
    override fun sendConfirmationEmail(destination: String, validationCode: String) {
        val subject = "Confirm your email"
        val text = "Please go to the next link to validate your account: " +
            "http://covidcontact.cat:8080/user/validate?code=$validationCode"
        sendEmail(destination, subject, text)
    }

    private fun sendEmail(destination: String, subject: String, text: String) {
        val message = SimpleMailMessage().apply {
            setFrom(mailSender.username!!)
            setTo(destination)
            setSubject(subject)
            setText(text)
        }

        mailSender.send(message)
    }
}
