package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.controllers.contactnetwork.ContactNetworkExceptions
import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.model.LimitParameters
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkState
import cat.covidcontact.server.model.nodes.member.Member
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostContactNetwork
import cat.covidcontact.server.services.user.NumberCalculatorService
import com.google.firebase.messaging.FirebaseMessaging

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
                addMember(currentUser, currentContactNetwork)
            } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
        } ?: throw UserExceptions.userDataNotFound
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

        addMember(owner, contactNetwork, isOwner = true)
        return contactNetwork
    }

    private fun addMember(
        user: User,
        contactNetwork: ContactNetwork,
        isOwner: Boolean = false
    ): User {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = isOwner))
        contactNetwork.memberEmails.add(user.email)
        ++contactNetwork.members
        updateContactNetworkState(contactNetwork)
        return userRepository.save(user)
    }

    private fun updateContactNetworkState(contactNetwork: ContactNetwork) {
        if (contactNetwork.state != ContactNetworkState.PositiveDetected) {
            val maxPeopleRecommended = LimitParameters.MAX_PEOPLE_RECOMMENDED
            val almostLimit = (maxPeopleRecommended * 0.75).toInt()

            contactNetwork.apply {
                state = when (members) {
                    in 0 until almostLimit -> ContactNetworkState.Normal
                    in almostLimit until maxPeopleRecommended -> ContactNetworkState.AlmostLimit
                    maxPeopleRecommended -> ContactNetworkState.Limit
                    else -> ContactNetworkState.OverLimit
                }
            }
        }
    }
}
