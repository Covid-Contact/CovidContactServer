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
import cat.covidcontact.server.model.nodes.location.CountryRepository
import cat.covidcontact.server.model.nodes.member.Member
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostRead
import cat.covidcontact.server.services.location.LocationService
import cat.covidcontact.server.services.messaging.MessagingService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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

    private lateinit var contactNetworks: List<ContactNetwork>
    private lateinit var users: List<User>
    private lateinit var devices: List<Device>
    private lateinit var reads: List<PostRead>

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
            User(email = "user1@gmail.com", username = "user1#1234").also { user ->
                user.contactNetworks.add(Member(contactNetwork = contactNetworks[0]))
                user.contactNetworks.add(Member(contactNetwork = contactNetworks[1]))
            },
            User(email = "user2@gmail.com", username = "user2#1234").also { user ->
                user.contactNetworks.add(Member(contactNetwork = contactNetworks[0]))
            },
            User(email = "user3@gmail.com", username = "user3#1234").also { user ->
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
            )
        )
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
}
