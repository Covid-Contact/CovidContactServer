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

import cat.covidcontact.server.controllers.CovidContactException
import cat.covidcontact.server.isEqualTo
import cat.covidcontact.server.model.LimitParameters
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.device.Device
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.device.UserDevice
import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.interaction.UserInteraction
import cat.covidcontact.server.model.nodes.location.*
import cat.covidcontact.server.model.nodes.member.Member
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostRead
import cat.covidcontact.server.services.location.LocationResponse
import cat.covidcontact.server.services.location.LocationService
import cat.covidcontact.server.services.messaging.MessagingService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InteractionServiceImplTest {
    private lateinit var interactionServiceImpl: InteractionServiceImpl

    @MockK
    private lateinit var deviceRepository: DeviceRepository

    @MockK
    private lateinit var interactionRepository: InteractionRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var contactNetworkRepository: ContactNetworkRepository

    @MockK
    private lateinit var countryRepository: CountryRepository

    @MockK
    private lateinit var locationService: LocationService

    @MockK
    private lateinit var messagingService: MessagingService

    private val countryName = "Spain"
    private val regionName = "Catalonia"
    private val provinceName = "Barcelona"
    private val cityName = "Barcelona"

    private lateinit var contactNetworks: List<ContactNetwork>
    private lateinit var users: List<User>
    private lateinit var devices: List<Device>
    private lateinit var reads: List<PostRead>
    private lateinit var locationResponse: LocationResponse
    private lateinit var country: Country

    @BeforeEach
    fun setUp() {
        deviceRepository = mockk()
        interactionRepository = mockk()
        userRepository = mockk()
        contactNetworkRepository = mockk()
        countryRepository = mockk()
        locationService = mockk()
        messagingService = mockk()

        interactionServiceImpl = InteractionServiceImpl(
            deviceRepository,
            interactionRepository,
            userRepository,
            contactNetworkRepository,
            countryRepository,
            locationService,
            messagingService
        )

        setUpDefaultData()
    }

    private fun setUpDefaultData() {
        contactNetworks = listOf(
            ContactNetwork(name = "ContactNetwork1"),
            ContactNetwork(name = "ContactNetwork2"),
        )

        users = listOf(
            User(
                email = "user1@gmail.com",
                username = "user1#1234",
                messagingToken = "1"
            ).also { user ->
                user.contactNetworks.add(Member(contactNetwork = contactNetworks[0]))
                user.contactNetworks.add(Member(contactNetwork = contactNetworks[1]))
            },
            User(
                email = "user2@gmail.com",
                username = "user2#1234",
                messagingToken = "2"
            ).also { user ->
                user.contactNetworks.add(Member(contactNetwork = contactNetworks[0]))
            },
            User(
                email = "user3@gmail.com",
                username = "user3#1234",
                messagingToken = "3"
            ).also { user ->
                user.contactNetworks.add(Member(contactNetwork = contactNetworks[0]))
            }
        )

        devices = listOf(
            Device(
                id = "1",
                name = "Device1",
                mutableListOf(
                    UserDevice(user = users[0], isLogged = true)
                )
            ),
            Device(
                id = "2",
                name = "Device2",
                mutableListOf(
                    UserDevice(user = users[1], isLogged = true)
                )
            ),
            Device(
                id = "3",
                name = "Device3",
                mutableListOf(
                    UserDevice(user = users[2], isLogged = true)
                )
            )
        )

        reads = listOf(
            PostRead(
                currentDeviceId = devices[0].id,
                deviceIds = listOf(devices[1].id),
                dateTime = 1,
                lat = null,
                lon = null
            ),
            PostRead(
                currentDeviceId = devices[0].id,
                deviceIds = listOf(devices[2].id),
                dateTime = 2,
                lat = null,
                lon = null
            ),
            PostRead(
                currentDeviceId = devices[0].id,
                deviceIds = emptyList(),
                dateTime = 3,
                lat = null,
                lon = null
            ),
            PostRead(
                currentDeviceId = devices[0].id,
                deviceIds = emptyList(),
                dateTime = LimitParameters.MIN_CONTAGIOUS_DURATION + 1L,
                lat = null,
                lon = null
            ),
            PostRead(
                currentDeviceId = devices[0].id,
                deviceIds = listOf(devices[1].id),
                dateTime = 1,
                lat = 1.0,
                lon = 1.0
            )
        )

        locationResponse = LocationResponse(
            country = countryName,
            region = regionName,
            province = provinceName,
            city = cityName
        )
        country = Country(name = countryName).also { country ->
            val region = Region(name = regionName).also { region ->
                val province = Province(name = provinceName).also { province ->
                    val city = City(name = cityName)
                    province.cities.add(city)
                }
                region.provinces.add(province)
            }
            country.regions.add(region)
        }
    }

    @Test
    fun `when current device does not exist then exception is thrown`() {
        every { deviceRepository.findDeviceById(any()) } returns null

        assertThrows<CovidContactException> {
            interactionServiceImpl.addRead(reads[0])
        }
    }

    @Test
    fun `when there is not any interaction in common contact networks then it is created`() {
        every { deviceRepository.findDeviceById(devices[0].id) } returns devices[0]
        every { deviceRepository.findDeviceById(devices[1].id) } returns devices[1]
        every {
            interactionRepository.getInteractionsByContactNetworkName(any())
        } returns emptyList()
        every { interactionRepository.saveAll(any<List<Interaction>>()) } returns emptyList()

        val interactions = interactionServiceImpl.addRead(reads[0])
        assertThat(interactions.size, isEqualTo(1))

        val interactionsList = interactions.toList()
        assertThat(
            interactionsList.first().userInteractions.all { userInteraction ->
                userInteraction.isEnded
            },
            isEqualTo(false)
        )
        assertThat(interactionsList.first().endDateTime, isEqualTo(null))
    }

    @Test
    fun `when there is already a not ended interaction then it is updated`() {
        every { deviceRepository.findDeviceById(devices[0].id) } returns devices[0]
        every { deviceRepository.findDeviceById(devices[2].id) } returns devices[2]
        every {
            interactionRepository.getInteractionsByContactNetworkName(any())
        } returns listOf(
            Interaction(
                startDateTime = 1,
                userInteractions = mutableListOf(
                    UserInteraction(user = users[0]),
                    UserInteraction(user = users[1])
                )
            )
        )
        every { interactionRepository.saveAll(any<List<Interaction>>()) } returns emptyList()

        val interactions = interactionServiceImpl.addRead(reads[1])
        assertThat(interactions.size, isEqualTo(1))

        val interactionsList = interactions.toList()
        assertThat(
            interactionsList.first().userInteractions.all { userInteraction ->
                userInteraction.isEnded
            },
            isEqualTo(false)
        )
        assertThat(interactionsList.first().endDateTime, isEqualTo(null))
    }

    @Test
    fun `when user has ended to send data then it is updated`() {
        every { deviceRepository.findDeviceById(devices[0].id) } returns devices[0]
        every {
            interactionRepository.getInteractionsByContactNetworkName(any())
        } returns listOf(
            Interaction(
                startDateTime = 1,
                userInteractions = mutableListOf(
                    UserInteraction(user = users[0]),
                    UserInteraction(user = users[1])
                )
            )
        )
        every { interactionRepository.saveAll(any<List<Interaction>>()) } returns emptyList()

        val interactions = interactionServiceImpl.addRead(reads[2])
        assertThat(interactions.size, isEqualTo(1))

        val interactionsList = interactions.toList()
        assertThat(
            interactionsList.first().userInteractions.find { userInteraction ->
                userInteraction.user.email == users[0].email
            }?.isEnded,
            isEqualTo(true)
        )
        assertThat(
            interactionsList.first().userInteractions.find { userInteraction ->
                userInteraction.user.email == users[1].email
            }?.isEnded,
            isEqualTo(false)
        )
        assertThat(interactionsList.first().endDateTime, isEqualTo(null))
    }

    @Test
    fun `when all users has ended to send their reads then the interaction is ended`() {
        every { deviceRepository.findDeviceById(devices[0].id) } returns devices[0]
        every {
            interactionRepository.getInteractionsByContactNetworkName(any())
        } returns listOf(
            Interaction(
                startDateTime = 1,
                userInteractions = mutableListOf(
                    UserInteraction(user = users[0]),
                    UserInteraction(user = users[1], isEnded = true)
                )
            )
        )
        every { interactionRepository.saveAll(any<List<Interaction>>()) } returns emptyList()

        val interactions = interactionServiceImpl.addRead(reads[2])
        assertThat(interactions.size, isEqualTo(1))

        val interactionsList = interactions.toList()
        assertThat(
            interactionsList.first().userInteractions.all { userInteraction ->
                userInteraction.isEnded
            },
            isEqualTo(true)
        )
        assertThat(interactionsList.first().endDateTime, not(isEqualTo(null)))
        assertThat(interactionsList.first().duration, isEqualTo(2))
        assertThat(interactionsList.first().isDangerous, isEqualTo(false))
    }

    @Test
    fun `when all users has sent all reads and duration is huge then interaction is dangerous`() {
        every { deviceRepository.findDeviceById(devices[0].id) } returns devices[0]
        every {
            interactionRepository.getInteractionsByContactNetworkName(any())
        } returns listOf(
            Interaction(
                startDateTime = 1,
                userInteractions = mutableListOf(
                    UserInteraction(user = users[0]),
                    UserInteraction(user = users[1], isEnded = true)
                )
            )
        )
        every { interactionRepository.saveAll(any<List<Interaction>>()) } returns emptyList()

        val interactions = interactionServiceImpl.addRead(reads[3])
        assertThat(interactions.size, isEqualTo(1))

        val interactionsList = interactions.toList()
        assertThat(
            interactionsList.first().userInteractions.all { userInteraction ->
                userInteraction.isEnded
            },
            isEqualTo(true)
        )
        assertThat(interactionsList.first().endDateTime, not(isEqualTo(null)))
        assertThat(
            interactionsList.first().duration,
            isEqualTo(LimitParameters.MIN_CONTAGIOUS_DURATION.toLong())
        )
        assertThat(interactionsList.first().isDangerous, isEqualTo(true))
    }

    @Test
    fun `when read contains the location then it is added to interaction`() {
        every { deviceRepository.findDeviceById(devices[0].id) } returns devices[0]
        every { deviceRepository.findDeviceById(devices[1].id) } returns devices[1]
        every {
            interactionRepository.getInteractionsByContactNetworkName(any())
        } returns emptyList()
        every { interactionRepository.saveAll(any<List<Interaction>>()) } returns emptyList()
        every { locationService.getLocationFromCoordinates(any(), any()) } returns locationResponse
        every { countryRepository.findCountryByName(any()) } returns country
        every { countryRepository.save(any()) } returns country

        val interactions = interactionServiceImpl.addRead(reads[4])

        val interactionsList = interactions.toList()
        assertThat(
            interactions.first().cities.find { city -> city.name == cityName } != null,
            isEqualTo(true)
        )
        assertThat(interactionsList.first().endDateTime, isEqualTo(null))
    }

    @Test
    fun `when notifying positive user does not exist then exception is thrown`() {
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            interactionServiceImpl.notifyPositive(users.first().email)
        }
    }

    @Test
    fun `when notifying positive users the close contacts are notified`() {
        every { userRepository.findByEmail(any()) } returns users.first()
        every { userRepository.getAllMembersFromContactNetwork(any()) } returns users
        every { userRepository.saveAll(any<List<User>>()) } returns users
        every {
            interactionRepository.getInteractionsByContactNetworkName(any())
        } returns listOf(
            Interaction(
                startDateTime = 1,
                userInteractions = mutableListOf(
                    UserInteraction(user = users[0]),
                    UserInteraction(user = users[1]),
                    UserInteraction(user = users[2])
                )
            )
        )
        every { contactNetworkRepository.save(any()) } returns contactNetworks.first()
        every { messagingService.sendMessage(any(), any(), any()) } returns Unit

        interactionServiceImpl.notifyPositive(users.first().email)

        verify(exactly = users.size) {
            messagingService.sendMessage(any(), any(), any())
        }
    }
}
