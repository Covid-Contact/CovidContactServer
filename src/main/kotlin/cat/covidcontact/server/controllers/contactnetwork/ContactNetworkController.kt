package cat.covidcontact.server.controllers.contactnetwork

import cat.covidcontact.server.controllers.*
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.post.PostContactNetwork
import cat.covidcontact.server.services.applicationuser.ApplicationUserService
import cat.covidcontact.server.services.contactnetwork.ContactNetworkService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ContactNetworkControllerUrls.BASE)
class ContactNetworkController(
    private val contactNetworkService: ContactNetworkService,
    private val applicationUserService: ApplicationUserService
) {

    @PostMapping(ContactNetworkControllerUrls.CREATE_CONTACT_NETWORK)
    fun createContactNetwork(@RequestBody postContactNetwork: PostContactNetwork) = runPost {
        val contactNetwork = contactNetworkService.createContactNetwork(postContactNetwork)
        contactNetwork.toPost()
    }

    @GetMapping(ContactNetworkControllerUrls.GET_CONTACT_NETWORKS)
    fun getContactNetworks(@RequestParam(required = true) email: String) = runGet {
        val contactNetworks = contactNetworkService.getContactNetworksFromUser(email)
        contactNetworks.map { contactNetwork -> contactNetwork.toPost() }
    }

    @PutMapping(ContactNetworkControllerUrls.ENABLE_USER_ADDITION)
    fun enableUserAddition(
        @PathVariable("name") name: String,
        @RequestParam(required = true) isEnabled: Boolean
    ) = runPut {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.enableUserAddition(contactNetworkName, isEnabled)
    }

    @PutMapping(ContactNetworkControllerUrls.GENERATE_ACCESS_CODE)
    fun generateAccessCode(@PathVariable("name") name: String) = runRequest(HttpStatus.OK) {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.generateAccessCode(contactNetworkName)
    }

    @GetMapping(ContactNetworkControllerUrls.GET_CONTACT_NETWORK_BY_ACCESS_CODE)
    fun getContactNetworkByAccessCode(@RequestParam(required = true) code: String) = runGet {
        val contactNetwork = contactNetworkService.getContactNetworkByAccessCode(code)
        contactNetwork.toPost()
    }

    @PutMapping(ContactNetworkControllerUrls.JOIN_CONTACT_NETWORK)
    fun joinContactNetwork(
        @PathVariable("name") name: String,
        @RequestParam(required = true) email: String
    ) = runPut {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.joinContactNetwork(contactNetworkName, email)
    }

    @DeleteMapping(ContactNetworkControllerUrls.EXIT_CONTACT_NETWORK)
    fun exitContactNetwork(
        @PathVariable("name") name: String,
        @RequestParam(required = true) email: String
    ) = runDelete {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.exitContactNetwork(contactNetworkName, email)
    }

    @DeleteMapping(ContactNetworkControllerUrls.DELETE_CONTACT_NETWORK)
    fun deleteContactNetwork(
        @PathVariable("name") name: String,
        @RequestParam(required = true) email: String
    ) = runDelete {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.deleteContactNetwork(contactNetworkName, email)
    }

    @PutMapping(ContactNetworkControllerUrls.UPDATE_CONTACT_NETWORK_VISIBILITY)
    fun updateVisibility(
        @PathVariable("name") name: String,
        @RequestParam isVisible: Boolean,
        @RequestParam(required = true) email: String
    ) = runPut {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.updateVisibility(contactNetworkName, email, isVisible)
    }

    @PutMapping(ContactNetworkControllerUrls.UPDATE_CONTACT_NETWORK_PASSWORD)
    fun updatePassword(
        @PathVariable("name") name: String,
        @RequestParam password: String,
        @RequestParam(required = true) email: String
    ) = runPut {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.updatePassword(contactNetworkName, password, email)
    }

    @PutMapping(ContactNetworkControllerUrls.UPDATE_CONTACT_NETWORK_IS_PASSWORD_PROTECTED)
    fun updatePasswordProtected(
        @PathVariable("name") name: String,
        @RequestParam(required = true) isProtected: Boolean,
        @RequestParam(required = true) email: String
    ) = runPut {
        val contactNetworkName = parseContactNetworkName(name)
        contactNetworkService.updateIsPasswordProtected(contactNetworkName, isProtected, email)
    }

    private fun parseContactNetworkName(name: String) =
        name.replace("%23", "#").replace("%20", " ")

    private fun ContactNetwork.toPost(): PostContactNetwork =
        PostContactNetwork(
            name = name,
            password = password,
            ownerUsername = ownerUsername,
            isVisible = isVisible,
            isPasswordProtected = isPasswordProtected,
            accessCode = accessCode,
            state = state.toString()
        )
}
