package cat.covidcontact.server.services.interaction

import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.post.PostRead

interface InteractionService {
    fun addRead(read: PostRead): Set<Interaction>
    fun notifyPositive(email: String)
}
