package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.controllers.contactnetwork.ContactNetworkExceptions
import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.data.contactnetwork.ContactNetwork
import cat.covidcontact.server.data.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.data.user.UserRepository
import cat.covidcontact.server.post.PostContactNetwork
import cat.covidcontact.server.services.user.NumberCalculatorService

class ContactNetworkServiceImpl(
    private val contactNetworkRepository: ContactNetworkRepository,
    private val userRepository: UserRepository,
    private val numberCalculatorService: NumberCalculatorService
) : ContactNetworkService {
    override fun createContactNetwork(postContactNetwork: PostContactNetwork): ContactNetwork {
        var createdContactNetwork: ContactNetwork? = null
        postContactNetwork.ownerEmail?.let { ownerEmail ->
            val user = userRepository.findByEmail(ownerEmail)

            user?.let { owner ->
                val existsContactNetwork =
                    contactNetworkRepository.existsContactNetworkByOwnerEmailAndNameStartingWith(
                        ownerEmail,
                        postContactNetwork.name
                    )
                if (existsContactNetwork) {
                    throw ContactNetworkExceptions.contactNetworkFoundForUser
                }
                createdContactNetwork = createContactNetwork(postContactNetwork, owner)
            } ?: UserExceptions.userDataNotFound
        } ?: throw ContactNetworkExceptions.ownerEmailNotFound

        return createdContactNetwork!!
    }

    private fun createContactNetwork(
        postContactNetwork: PostContactNetwork,
        owner: User
    ): ContactNetwork {
        val code = numberCalculatorService.generateRandomNumber()
        val contactNetwork = ContactNetwork(
            name = "${postContactNetwork.name}#$code",
            password = postContactNetwork.password,
            owner = owner,
            members = listOf(owner)
        )

        contactNetworkRepository.save(contactNetwork)
        return contactNetwork
    }
}