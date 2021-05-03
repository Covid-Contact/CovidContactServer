package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.post.PostContactNetwork

interface ContactNetworkService {
    fun createContactNetwork(postContactNetwork: PostContactNetwork): ContactNetwork

    fun getContactNetworksFromUser(email: String): List<ContactNetwork>

    fun enableUserAddition(contactNetworkName: String, isEnabled: Boolean)

    fun generateAccessCode(contactNetworkName: String): String

    fun getContactNetworkByAccessCode(accessCode: String): ContactNetwork

    fun joinContactNetwork(contactNetworkName: String, email: String)
}
