package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.data.contactnetwork.ContactNetwork
import cat.covidcontact.server.post.PostContactNetwork

interface ContactNetworkService {
    fun createContactNetwork(postContactNetwork: PostContactNetwork): ContactNetwork
}
