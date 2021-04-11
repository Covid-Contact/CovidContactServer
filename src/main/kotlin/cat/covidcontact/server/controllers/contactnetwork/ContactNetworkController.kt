package cat.covidcontact.server.controllers.contactnetwork

import cat.covidcontact.server.controllers.runGet
import cat.covidcontact.server.controllers.runPost
import cat.covidcontact.server.post.PostContactNetwork
import cat.covidcontact.server.services.contactnetwork.ContactNetworkService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ContactNetworkControllerUrls.BASE)
class ContactNetworkController(
    private val contactNetworkService: ContactNetworkService
) {

    @PostMapping(ContactNetworkControllerUrls.CREATE_CONTACT_NETWORK)
    fun createContactNetwork(@RequestBody postContactNetwork: PostContactNetwork) = runPost {
        val contactNetwork = contactNetworkService.createContactNetwork(postContactNetwork)
        PostContactNetwork(
            name = contactNetwork.name,
            password = contactNetwork.password,
        )
    }

    @GetMapping(ContactNetworkControllerUrls.GET_CONTACT_NETWORKS)
    fun getContactNetworks(@RequestParam(required = true) email: String) = runGet {
        val contactNetworks = contactNetworkService.getContactNetworksFromUser(email)
        contactNetworks.map { contactNetwork ->
            PostContactNetwork(
                name = contactNetwork.name,
                password = contactNetwork.password
            )
        }
    }
}
