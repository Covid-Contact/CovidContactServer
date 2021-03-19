package cat.covidcontact.server.services.email

interface EmailService {
    fun sendConfirmationEmail(destination: String, validationCode: Int)
}