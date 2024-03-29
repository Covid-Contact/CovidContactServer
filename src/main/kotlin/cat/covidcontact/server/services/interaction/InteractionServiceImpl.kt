/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cat.covidcontact.server.services.interaction

import cat.covidcontact.server.controllers.interaction.InteractionExceptions
import cat.covidcontact.server.model.LimitParameters
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkState
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.interaction.UserInteraction
import cat.covidcontact.server.model.nodes.location.*
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.nodes.user.UserState
import cat.covidcontact.server.model.post.PostRead
import cat.covidcontact.server.services.location.LocationService
import cat.covidcontact.server.services.messaging.MessagingService
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min

@Service
class InteractionServiceImpl(
    private val deviceRepository: DeviceRepository,
    private val interactionRepository: InteractionRepository,
    private val userRepository: UserRepository,
    private val contactNetworkRepository: ContactNetworkRepository,
    private val countryRepository: CountryRepository,
    private val locationService: LocationService,
    private val messagingService: MessagingService
) : InteractionService {

    @Synchronized
    override fun addRead(read: PostRead): Set<Interaction> {
        val currentUser = getUserFromDevice(read.currentDeviceId) ?: return emptySet()
        val currentUserContactNetworks = currentUser.getContactNetworks()

        val interactions = if (read.deviceIds.isEmpty()) {
            registerSendEnding(read, currentUser, currentUserContactNetworks)
        } else {
            registerInteractionsToContactNetworks(read, currentUserContactNetworks, currentUser)
        }

        if (read.lat != null && read.lon != null) {
            addLocation(read, interactions)
        }

        return interactions
    }

    @Synchronized
    override fun notifyPositive(email: String) {
        userRepository.findByEmail(email)?.let { positiveUser ->
            val sendingUsers: MutableSet<User> = mutableSetOf()

            positiveUser.contactNetworks.forEach { member ->
                val contactNetwork = member.contactNetwork
                val currentTime = System.currentTimeMillis()
                val periodStart = currentTime - LimitParameters.CONTAGIOUS_PERIOD

                val interactions = interactionRepository.getInteractionsByContactNetworkName(
                    contactNetwork.name
                ).filter { interaction ->
                    interaction.endDateTime == null ||
                        (interaction.isDangerous!! &&
                            interaction.startDateTime in periodStart..currentTime)
                }

                if (contactNetwork.state != ContactNetworkState.Deleted) {
                    contactNetwork.state = ContactNetworkState.PositiveDetected
                    contactNetworkRepository.save(contactNetwork)
                }

                val closeContacts = interactions.getAllUsers().map { user -> user.email }
                val users = userRepository.getAllMembersFromContactNetwork(contactNetwork.name)
                    .onEach { user ->
                        if (user.state != UserState.Quarantine) {
                            user.state = when (user.email) {
                                in closeContacts -> UserState.Quarantine
                                else -> UserState.Prevention
                            }
                        }

                        sendingUsers.add(user)
                    }

                userRepository.saveAll(users)
            }

            sendingUsers.forEach { user ->
                sendCurrentState(user)
            }
        } ?: throw InteractionExceptions.userNotFound
    }

    private fun registerSendEnding(
        read: PostRead,
        user: User,
        contactNetworks: List<ContactNetwork>
    ): Set<Interaction> {
        val currentInteractions: MutableSet<Interaction> = mutableSetOf()

        contactNetworks.forEach { contactNetwork ->
            val interactions = interactionRepository.getInteractionsByContactNetworkName(
                contactNetwork.name
            ).getNotEndedUserInteractions(user)

            interactions.forEach { interaction ->
                finishUserSending(interaction, user, read)
            }

            interactionRepository.saveAll(interactions)
            currentInteractions.addAll(interactions)
        }

        return currentInteractions
    }

    private fun finishUserSending(
        interaction: Interaction,
        user: User,
        read: PostRead
    ) {
        interaction.userInteractions.find { it.user.email == user.email }?.isEnded = true

        if (interaction.userInteractions.all { it.isEnded }) {
            val originalStart = interaction.startDateTime
            interaction.apply {
                startDateTime = min(originalStart, read.dateTime)
                endDateTime = max(originalStart, read.dateTime)
                duration = endDateTime!! - startDateTime
                isDangerous = duration!! >= LimitParameters.MIN_CONTAGIOUS_DURATION
            }
        }
    }

    private fun registerInteractionsToContactNetworks(
        read: PostRead,
        currentUserContactNetworks: List<ContactNetwork>,
        currentUser: User
    ): Set<Interaction> {
        val currentInteractions: MutableSet<Interaction> = mutableSetOf()

        read.deviceIds.forEach { deviceId ->
            val user = getUserFromDevice(deviceId) ?: throw InteractionExceptions.deviceNotFound
            val contactNetworks = user.getContactNetworks()
            val commonContactNetworks = currentUserContactNetworks intersect contactNetworks

            commonContactNetworks.forEach { contactNetwork ->
                val savedInteractions = registerInteraction(
                    contactNetwork = contactNetwork,
                    currentUser = currentUser,
                    read = read,
                    user = user
                )

                currentInteractions.addAll(savedInteractions)
            }
        }

        return currentInteractions
    }

    private fun registerInteraction(
        contactNetwork: ContactNetwork,
        currentUser: User,
        read: PostRead,
        user: User
    ): List<Interaction> {
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
        return interactions
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
        return device.users.find { userDevice -> userDevice.isLogged }?.user
    }

    private fun sendCurrentState(user: User) {
        println("Sent message to ${user.email} with ${user.state}")
        user.messagingToken?.let { token ->
            messagingService.sendMessage(token, STATE, user.state.toString())
        }
    }

    private fun addLocation(read: PostRead, interactions: Set<Interaction>) {
        if (read.lat != null && read.lon != null) {
            val locationResponse = locationService.getLocationFromCoordinates(
                lat = read.lat!!,
                lon = read.lon!!
            )

            val countryName = locationResponse.country
            val regionName = locationResponse.region
            val provinceName = locationResponse.province
            val cityName = locationResponse.city

            if (!countryName.isNullOrEmpty() && !regionName.isNullOrEmpty()
                && !provinceName.isNullOrEmpty() && !cityName.isNullOrEmpty()
            ) {
                val country =
                    countryRepository.findCountryByName(countryName) ?: Country(countryName)

                val region = country.regions.find { region -> region.name == regionName }
                    ?: Region(regionName).also { region ->
                        country.regions.add(region)
                    }

                val province = region.provinces.find { province -> province.name == provinceName }
                    ?: Province(provinceName).also { province ->
                        region.provinces.add(province)
                    }

                val city = province.cities.find { city -> city.name == cityName }
                    ?: City(cityName).also { city ->
                        province.cities.add(city)
                    }

                interactions.forEach { interaction -> interaction.cities.add(city) }
                interactionRepository.saveAll(interactions)
                countryRepository.save(country)
            }
        }
    }

    private fun User.getContactNetworks(): List<ContactNetwork> =
        contactNetworks.map { member -> member.contactNetwork }

    private fun List<Interaction>.getNotEndedUserInteractions(
        user: User
    ): MutableList<Interaction> = filter { interaction ->
        interaction.endDateTime == null && interaction.userInteractions.containsUser(user)
    }.toMutableList()

    private fun List<UserInteraction>.containsUser(user: User): Boolean =
        find { it.user.email == user.email } != null

    private fun List<Interaction>.getAllUsers(): List<User> =
        flatMap { interaction -> interaction.userInteractions }
            .map { userInteraction -> userInteraction.user }
            .toSet()
            .toList()

    companion object {
        private const val STATE = "State"
    }
}
