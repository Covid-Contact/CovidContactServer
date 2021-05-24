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
import com.google.firebase.messaging.FirebaseMessaging
import java.security.MessageDigest

class ContactNetworkServiceImpl(
    private val contactNetworkRepository: ContactNetworkRepository,
    private val userRepository: UserRepository,
    private val numberCalculatorService: NumberCalculatorService,
    private val firebaseMessaging: FirebaseMessaging
) : ContactNetworkService {

    @Synchronized
    override fun createContactNetwork(
        postContactNetwork: PostContactNetwork,
        ownerMessageToken: String
    ): ContactNetwork {
        return postContactNetwork.ownerEmail?.let { ownerEmail ->
            userRepository.findByEmail(ownerEmail)?.let { owner ->
                val contactNetworkExistsForUser = owner.contactNetworks
                    .find { it.contactNetwork.name.startsWith(postContactNetwork.name) } != null
                if (contactNetworkExistsForUser) {
                    throw ContactNetworkExceptions.contactNetworkFoundForUser
                }
                createContactNetwork(postContactNetwork, ownerMessageToken, owner)
            } ?: throw UserExceptions.userDataNotFound
        } ?: throw ContactNetworkExceptions.ownerEmailNotFound

    }

    @Synchronized
    override fun getContactNetworksFromUser(email: String): List<ContactNetwork> {
        val user = userRepository.findByEmail(email)
        return user?.contactNetworks?.map { it.contactNetwork }
            ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun enableUserAddition(contactNetworkName: String, isEnabled: Boolean) {
        val contactNetwork = contactNetworkRepository.findContactNetworkByName(contactNetworkName)
        contactNetwork?.let {
            it.isVisible = isEnabled
            contactNetworkRepository.save(contactNetwork)
        } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
    }

    @Synchronized
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

    @Synchronized
    override fun getContactNetworkByAccessCode(accessCode: String): ContactNetwork {
        val contactNetwork = contactNetworkRepository.findContactNetworkByAccessCode(accessCode)
        return contactNetwork ?: throw ContactNetworkExceptions.invalidAccessCode
    }

    @Synchronized
    override fun joinContactNetwork(
        contactNetworkName: String,
        email: String,
        messageToken: String
    ) {
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

        joinNotificationTopic(contactNetworkName, messageToken)
    }

    private fun createContactNetwork(
        postContactNetwork: PostContactNetwork,
        ownerMessageToken: String,
        owner: User
    ): ContactNetwork {
        val code = numberCalculatorService.generateRandomNumber()
        val contactNetwork = ContactNetwork(
            name = "${postContactNetwork.name}#$code",
            password = postContactNetwork.password,
            ownerUsername = owner.username
        )

        joinNotificationTopic(contactNetwork.name, ownerMessageToken)

        owner.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        userRepository.save(owner)

        return contactNetwork
    }

    private fun joinNotificationTopic(contactNetworkName: String, ownerMessageToken: String) {
        firebaseMessaging.subscribeToTopic(listOf(ownerMessageToken), contactNetworkName.sha512())
    }

    private fun String.sha512(): String =
        MessageDigest.getInstance("SHA-512")
            .digest(toByteArray())
            .map { byte -> Integer.toHexString(0xFF and byte.toInt()) }
            .map { byte -> if (byte.length < 2) "0$byte" else byte }
            .fold("") { total, actual -> total + actual }
}
