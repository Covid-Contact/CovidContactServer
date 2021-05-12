package cat.covidcontact.server.services.interaction

import cat.covidcontact.server.controllers.interaction.InteractionExceptions
import cat.covidcontact.server.model.LimitParameters
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.interaction.UserInteraction
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostRead

class InteractionServiceImpl(
    private val deviceRepository: DeviceRepository,
    private val interactionRepository: InteractionRepository
) : InteractionService {

    override fun addRead(read: PostRead) {
        val currentUser = getUserFromDevice(read.currentDeviceId) ?: return
        val currentUserContactNetworks = currentUser.getContactNetworks()

        if (read.deviceIds.isEmpty()) {
            registerSendEnding(read, currentUser, currentUserContactNetworks)
        } else {
            registerInteractionsToContactNetworks(read, currentUserContactNetworks, currentUser)
        }
    }

    private fun registerSendEnding(
        read: PostRead,
        user: User,
        contactNetworks: List<ContactNetwork>
    ) {
        contactNetworks.forEach { contactNetwork ->
            val interactions = interactionRepository.getInteractionsByContactNetworkName(
                contactNetwork.name
            ).getNotEndedUserInteractions(user)

            interactions.forEach { interaction ->
                finishUserSending(interaction, user, read)
            }

            interactionRepository.saveAll(interactions)
        }
    }

    private fun finishUserSending(
        interaction: Interaction,
        user: User,
        read: PostRead
    ) {
        interaction.userInteractions.find { it.user.email == user.email }?.isEnded = true

        if (interaction.userInteractions.all { it.isEnded }) {
            interaction.apply {
                endDateTime = read.dateTime
                duration = endDateTime!! - startDateTime
                isDangerous = duration!! >= LimitParameters.MIN_CONTAGIOUS_DURATION
            }
        }
    }

    private fun registerInteractionsToContactNetworks(
        read: PostRead,
        currentUserContactNetworks: List<ContactNetwork>,
        currentUser: User
    ) {
        read.deviceIds.forEach { deviceId ->
            val user = getUserFromDevice(deviceId) ?: throw InteractionExceptions.deviceNotFound
            val contactNetworks = user.getContactNetworks()
            val commonContactNetworks = currentUserContactNetworks intersect contactNetworks

            commonContactNetworks.forEach { contactNetwork ->
                registerInteraction(contactNetwork, currentUser, read, user)
            }
        }
    }

    private fun registerInteraction(
        contactNetwork: ContactNetwork,
        currentUser: User,
        read: PostRead,
        user: User
    ) {
        val interactions = interactionRepository.getInteractionsByContactNetworkName(
            contactNetwork.name
        ).getNotEndedUserInteractions(currentUser)

        if (interactions.isEmpty()) {
            addNewInteraction(interactions, read, currentUser, contactNetwork)
        }

        interactions.forEach { interaction ->
            updateInteraction(interaction, user)
        }

        interactionRepository.saveAll(interactions)
    }

    private fun updateInteraction(interaction: Interaction, user: User) {
        var userInteraction = interaction.userInteractions.find { it.user.email == user.email }
        if (userInteraction == null) {
            userInteraction = UserInteraction(user = user)
            interaction.userInteractions.add(userInteraction)
        }

        userInteraction.isEnded = false
    }

    private fun addNewInteraction(
        interactions: MutableList<Interaction>,
        read: PostRead,
        currentUser: User,
        contactNetwork: ContactNetwork
    ) {
        interactions.add(
            Interaction(
                startDateTime = read.dateTime,
                userInteractions = mutableListOf(UserInteraction(user = currentUser)),
                contactNetwork = contactNetwork
            )
        )
    }

    private fun getUserFromDevice(deviceId: String): User? {
        val device = deviceRepository.findDeviceById(deviceId)
            ?: throw InteractionExceptions.deviceNotFound
        return device.users.find { it.isLogged }?.user
    }

    private fun User.getContactNetworks(): List<ContactNetwork> =
        contactNetworks.map { it.contactNetwork }

    private fun List<Interaction>.getNotEndedUserInteractions(
        user: User
    ): MutableList<Interaction> = filter { interaction ->
        interaction.endDateTime == null && interaction.userInteractions.containsUser(user)
    }.toMutableList()

    private fun List<UserInteraction>.containsUser(user: User): Boolean =
        find { it.user.email == user.email } != null
}
