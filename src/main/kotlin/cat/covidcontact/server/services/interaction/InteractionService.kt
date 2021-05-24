package cat.covidcontact.server.services.interaction

import cat.covidcontact.server.model.post.PostRead

interface InteractionService {
    fun addRead(read: PostRead)
    fun notifyPositive(email: String)
}
