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
import org.springframework.stereotype.Service

@Service
class ContactNetworkServiceImpl(
    private val contactNetworkRepository: ContactNetworkRepository,
    private val userRepository: UserRepository,
    private val numberCalculatorService: NumberCalculatorService
) : ContactNetworkService {
    private val ignoredContactNetworkStates = listOf(
        ContactNetworkState.PositiveDetected,
        ContactNetworkState.Deleted
    )

    @Synchronized
    override fun createContactNetwork(postContactNetwork: PostContactNetwork): ContactNetwork {
        return postContactNetwork.ownerEmail?.let { ownerEmail ->
            userRepository.findByEmail(ownerEmail)?.let { owner ->
                val contactNetworkExistsForUser = owner.contactNetworks
                    .find { it.contactNetwork.name.startsWith(postContactNetwork.name) } != null
                if (contactNetworkExistsForUser) {
                    throw ContactNetworkExceptions.contactNetworkFoundForUser
                }
                createContactNetwork(postContactNetwork, owner)
            } ?: throw UserExceptions.userDataNotFound
        } ?: throw ContactNetworkExceptions.ownerEmailNotFound

    }

    @Synchronized
    override fun getContactNetworksFromUser(email: String): List<ContactNetwork> {
        return userRepository.findByEmail(email)?.contactNetworks?.map { it.contactNetwork }
            ?.filter { it.state != ContactNetworkState.Deleted }
            ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun enableUserAddition(contactNetworkName: String, isEnabled: Boolean) {
        contactNetworkRepository.findContactNetworkByName(contactNetworkName)
            ?.let { contactNetwork ->
                contactNetwork.isVisible = isEnabled
                contactNetworkRepository.save(contactNetwork)
            } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
    }

    @Synchronized
    override fun generateAccessCode(contactNetworkName: String): String {
        return contactNetworkRepository.findContactNetworkByName(contactNetworkName)
            ?.let { network ->
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
        return contactNetworkRepository.findContactNetworkByAccessCode(accessCode)
            ?: throw ContactNetworkExceptions.invalidAccessCode
    }

    @Synchronized
    override fun joinContactNetwork(
        contactNetworkName: String,
        email: String
    ) {
        userRepository.findByEmail(email)?.let { currentUser ->
            val member = currentUser.contactNetworks.find {
                it.contactNetwork.name == contactNetworkName
            }

            if (member != null) {
                throw ContactNetworkExceptions.userAlreadyJoined
            }

            contactNetworkRepository.findContactNetworkByName(contactNetworkName)
                ?.let { currentContactNetwork ->
                    addMember(currentUser, currentContactNetwork)
                } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
        } ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun exitContactNetwork(contactNetworkName: String, email: String) {
        userRepository.findByEmail(email)?.let { user ->
            contactNetworkRepository.findContactNetworkByName(contactNetworkName)
                ?.let { contactNetwork ->
                    if (!isOwner(user, contactNetwork)) {
                        removeMember(user, contactNetwork)
                    }
                } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
        } ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun deleteContactNetwork(name: String, email: String) =
        runIfOwner(name, email) { contactNetwork ->
            contactNetwork.state = ContactNetworkState.Deleted
            contactNetworkRepository.setStateToDeleted(contactNetwork.name)
        }

    @Synchronized
    override fun updateVisibility(name: String, email: String, isVisible: Boolean) =
        runIfOwner(name, email) { contactNetwork ->
            contactNetwork.isVisible = isVisible
            contactNetworkRepository.save(contactNetwork)
        }

    @Synchronized
    override fun updatePassword(name: String, password: String, email: String) =
        runIfOwner(name, email) { contactNetwork ->
            contactNetwork.password = password
            contactNetworkRepository.save(contactNetwork)
        }

    @Synchronized
    override fun updateIsPasswordProtected(
        name: String,
        isProtected: Boolean,
        email: String
    ) = runIfOwner(name, email) { contactNetwork ->
        contactNetwork.isPasswordProtected = isProtected
        contactNetworkRepository.save(contactNetwork)
    }

    @Synchronized
    override fun getContactNetworkIfNotMember(
        contactNetworkName: String,
        email: String
    ): List<ContactNetwork> {
        val members = userRepository.getAllMembersFromContactNetwork(contactNetworkName)
        return if (members.find { user -> user.email == email } == null) {
            contactNetworkRepository.findContactNetworkByName(contactNetworkName)
                ?.let { contactNetwork -> listOf(contactNetwork) } ?: listOf()
        } else {
            listOf()
        }
    }

    @Synchronized
    override fun deleteMember(contactNetworkName: String, memberEmail: String, email: String) {
        runIfOwner(contactNetworkName, email) {
            userRepository.removeMember(memberEmail, contactNetworkName)
        }
    }

    private fun isOwner(user: User, contactNetwork: ContactNetwork) =
        user.contactNetworks.find { member ->
            member.contactNetwork.name == contactNetwork.name && member.isOwner
        } != null

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

        addMember(owner, contactNetwork, isOwner = true)
        return contactNetwork
    }

    private fun addMember(
        user: User,
        contactNetwork: ContactNetwork,
        isOwner: Boolean = false
    ): User {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = isOwner))
        ++contactNetwork.members
        updateContactNetworkState(contactNetwork)
        return userRepository.save(user)
    }

    private fun removeMember(
        user: User,
        contactNetwork: ContactNetwork
    ) {
        user.contactNetworks.removeIf { member ->
            member.contactNetwork.name == contactNetwork.name
        }

        userRepository.removeMember(user.email, contactNetwork.name)

        --contactNetwork.members
        updateContactNetworkState(contactNetwork)
        contactNetworkRepository.save(contactNetwork)
    }

    private fun updateContactNetworkState(contactNetwork: ContactNetwork) {
        if (!ignoredContactNetworkStates.contains(contactNetwork.state)) {
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

    private fun runIfOwner(name: String, email: String, onOwnerVerified: (ContactNetwork) -> Unit) {
        contactNetworkRepository.findContactNetworkByName(name)?.let { contactNetwork ->
            userRepository.findByEmail(email)?.let { user ->
                if (!isOwner(user, contactNetwork)) {
                    throw ContactNetworkExceptions.userIsNotOwner
                }

                onOwnerVerified(contactNetwork)
            } ?: throw UserExceptions.userDataNotFound
        } ?: throw ContactNetworkExceptions.contactNetworkNotExisting
    }
}
