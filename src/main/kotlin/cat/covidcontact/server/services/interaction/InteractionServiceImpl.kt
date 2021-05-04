package cat.covidcontact.server.services.interaction

import cat.covidcontact.server.controllers.interaction.InteractionExceptions
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.interaction.UserInteraction
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostRead

class InteractionServiceImpl(
    private val deviceRepository: DeviceRepository,
    private val contactNetworkRepository: ContactNetworkRepository,
    private val interactionRepository: InteractionRepository
) : InteractionService {

    override fun addRead(read: PostRead) {
        val currentUser = getUserFromDevice(read.currentDeviceId) ?: return
        val currentUserContactNetworks = currentUser.getContactNetworks()

        println("Read: $read")
        println("Current user: ${currentUser.email}")
        println("Current user contact networks: ${currentUserContactNetworks.map { it.name }}")
        println("----------")

        read.deviceIds.forEach { deviceId ->
            val user = getUserFromDevice(deviceId) ?: throw InteractionExceptions.deviceNotFound
            val contactNetworks = user.getContactNetworks()
            val commonContactNetworks = currentUserContactNetworks intersect contactNetworks

            println("User: ${user.email}")
            println("User contact networks: ${contactNetworks.map { it.name }}")
            println("User contact networks common: ${commonContactNetworks.map { it.name }}")
            println("----------")

            commonContactNetworks.forEach { contactNetwork ->
                println("Starting ${contactNetwork.name}")
                registerInteraction(contactNetwork, currentUser, read, user)
                contactNetworkRepository.save(contactNetwork)
            }
        }
    }

    private fun registerInteraction(
        contactNetwork: ContactNetwork,
        currentUser: User,
        read: PostRead,
        user: User
    ) {
        val interactions = contactNetwork.interactions
            .getNotEndedUserInteractions(currentUser)

        println("Current interactions: ${interactions.map { int -> int.userInteractions.map { it.user.email } }}")

        if (interactions.isEmpty()) {
            interactions.add(
                Interaction(
                    startDateTime = read.dateTime,
                    userInteractions = mutableListOf(UserInteraction(user = currentUser))
                )
            )
        }

        interactions.forEach { interaction ->
            if (!interaction.userInteractions.map { it.user.email }.contains(user.email)) {
                interaction.userInteractions.add(UserInteraction(user = user))
            }
        }

        println("After interactions: ${interactions.map { int -> int.userInteractions.map { it.user.email } }}")
        //interactionRepository.saveAll(interactions)
        contactNetwork.interactions.addAll(interactions)
        println(contactNetwork.interactions.map { int -> int.userInteractions.map { it.user.email } })
        println("----------")
    }

    private fun getUserFromDevice(deviceId: String): User? {
        val device = deviceRepository.findDeviceById(deviceId)
            ?: throw InteractionExceptions.deviceNotFound
        return device.users.find { it.isLogged }?.user
    }

    private fun User.getContactNetworks(): List<ContactNetwork> =
        contactNetworks.map { it.contactNetwork }

    private fun Set<Interaction>.getNotEndedUserInteractions(
        user: User
    ): MutableList<Interaction> = filter { interaction ->
        interaction.endDateTime == null && interaction.userInteractions.containsUser(user)
    }.toMutableList()

    private fun List<UserInteraction>.containsUser(user: User): Boolean =
        find { it.user.email == user.email } != null
}
