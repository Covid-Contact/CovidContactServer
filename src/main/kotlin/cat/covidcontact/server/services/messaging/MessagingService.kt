package cat.covidcontact.server.services.messaging

interface MessagingService {
    fun sendMessage(token: String, key: String, data: String)
}
