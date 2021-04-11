package cat.covidcontact.server.controllers.contactnetwork

import cat.covidcontact.server.controllers.runPost
import cat.covidcontact.server.post.PostContactNetwork
import cat.covidcontact.server.services.contactnetwork.ContactNetworkService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ContactNetworkControllerUrls.base)
class ContactNetworkController(
    private val contactNetworkService: ContactNetworkService
) {

    @PostMapping("/")
    fun createContactNetwork(@RequestBody postContactNetwork: PostContactNetwork) = runPost {
        val contactNetwork = contactNetworkService.createContactNetwork(postContactNetwork)
        PostContactNetwork(
            name = contactNetwork.name,
            password = contactNetwork.password,
            ownerUsername = contactNetwork.owner.username
        )
    }
}
