package cat.covidcontact.server.services.messaging

import cat.covidcontact.server.security.decrypt
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.stereotype.Service

@Service
class MessagingServiceImpl(
    private val firebaseMessaging: FirebaseMessaging
) : MessagingService {

    override fun sendMessage(token: String, key: String, data: String) {
        val message = Message.builder()
            .setToken(token.decrypt())
            .putData(key, data)
            .build()
        firebaseMessaging.send(message)
    }
}
