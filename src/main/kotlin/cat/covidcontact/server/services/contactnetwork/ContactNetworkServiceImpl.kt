package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.controllers.contactnetwork.ContactNetworkExceptions
import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.member.Member
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostContactNetwork
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
                val contactNetworkExistsForUser = user.contactNetworks
                    .find { it.contactNetwork.name.startsWith(postContactNetwork.name) } != null
                if (contactNetworkExistsForUser) {
                    throw ContactNetworkExceptions.contactNetworkFoundForUser
                }
                createdContactNetwork = createContactNetwork(postContactNetwork, owner)
            } ?: UserExceptions.userDataNotFound
        } ?: throw ContactNetworkExceptions.ownerEmailNotFound

        return createdContactNetwork!!
    }

    override fun getContactNetworksFromUser(email: String): List<ContactNetwork> {
        val user = userRepository.findByEmail(email)
        return user?.contactNetworks?.map { it.contactNetwork }
            ?: throw UserExceptions.userDataNotFound
    }

    override fun enableUserAddition(contactNetworkName: String, isEnabled: Boolean) {
        val contactNetwork = contactNetworkRepository.findContactNetworkByName(contactNetworkName)
        contactNetwork?.let {
            it.isVisible = isEnabled
            contactNetworkRepository.save(contactNetwork)
        } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
    }

    override fun generateAccessCode(contactNetworkName: String): String {
        val contactNetwork = contactNetworkRepository.findContactNetworkByName(contactNetworkName)
        return contactNetwork?.let { network ->
            var accessCode = numberCalculatorService.generateAccessCode()
            while (contactNetworkRepository.existsContactNetworkByAccessCode(accessCode)) {
                accessCode = numberCalculatorService.generateAccessCode()
            }

            network.accessCode = accessCode
            contactNetworkRepository.save(network)
            accessCode
        } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
    }

    override fun getContactNetworkByAccessCode(accessCode: String): ContactNetwork {
        val contactNetwork = contactNetworkRepository.findContactNetworkByAccessCode(accessCode)
        return contactNetwork ?: throw ContactNetworkExceptions.invalidAccessCode
    }

    override fun joinContactNetwork(contactNetworkName: String, email: String) {
        val user = userRepository.findByEmail(email)
        user?.let { currentUser ->
            val member = currentUser.contactNetworks.find {
                it.contactNetwork.name == contactNetworkName
            }

            if (member != null) {
                throw ContactNetworkExceptions.userAlreadyJoined
            }

            val contactNetwork =
                contactNetworkRepository.findContactNetworkByName(contactNetworkName)
            contactNetwork?.let { currentContactNetwork ->
                currentUser.contactNetworks.add(Member(contactNetwork = currentContactNetwork))
                userRepository.save(currentUser)
            } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
        } ?: throw UserExceptions.userDataNotFound
    }

    private fun createContactNetwork(
        postContactNetwork: PostContactNetwork,
        owner: User
    ): ContactNetwork {
        val code = numberCalculatorService.generateRandomNumber()
        val contactNetwork = ContactNetwork(
            name = "${postContactNetwork.name}#$code",
            password = postContactNetwork.password,
            ownerUsername = owner.username
        )

        owner.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        userRepository.save(owner)
        return contactNetwork
    }
}