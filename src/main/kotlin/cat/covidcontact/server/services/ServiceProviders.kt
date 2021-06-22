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

package cat.covidcontact.server.services

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class ServiceProviders {
    private val noReplyPassword = System.getenv("COVID_CONTACT_NO_REPLY")

    @Bean
    fun provideJavaMailService(): JavaMailSenderImpl = JavaMailSenderImpl().apply {
        host = "smtp.gmail.com"
        port = 587
        username = "noreply.covidcontact@gmail.com"
        password = noReplyPassword
        javaMailProperties.let { props ->
            props["mail.transport.protocol"] = "smtp"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.debug"] = "true"
        }
    }
}
