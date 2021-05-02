package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.data.contactnetwork.ContactNetwork
import cat.covidcontact.server.post.PostContactNetwork

interface ContactNetworkService {
    fun createContactNetwork(postContactNetwork: PostContactNetwork): ContactNetwork

    fun getContactNetworksFromUser(email: String): List<ContactNetwork>

    fun enableUserAddition(contactNetworkName: String, isEnabled: Boolean)

    fun generateAccessCode(contactNetworkName: String): String

    fun getContactNetworkByAccessCode(accessCode: String): ContactNetwork
}
