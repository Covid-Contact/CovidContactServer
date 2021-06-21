package cat.covidcontact.server.services.device

import cat.covidcontact.server.isEqualTo
import cat.covidcontact.server.model.nodes.device.Device
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.device.UserDevice
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostDevice
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeviceServiceImplTest {
    private lateinit var deviceServiceImpl: DeviceServiceImpl

    @MockK
    private lateinit var deviceRepository: DeviceRepository

    private val email = "albert@gmail.com"
    private val username = "Albert#1234"

    private lateinit var user: User
    private lateinit var devices: List<Device>
    private lateinit var postDevice: PostDevice

    @BeforeEach
    fun setUp() {
        deviceRepository = mockk()
        deviceServiceImpl = DeviceServiceImpl(deviceRepository)

        setUpDefaultData()
    }

    private fun setUpDefaultData() {
        user = User(
            email = email,
            username = username
        )

        devices = listOf(
            Device(
                id = "1",
                name = "Device1",
                mutableListOf(
                    UserDevice(user = user, isLogged = true)
                )
            ),
            Device(
                id = "2",
                name = "2",
                mutableListOf(
                    UserDevice(user = user, isLogged = false)
                )
            )
        )

        postDevice = PostDevice(
            id = devices.last().id,
            name = devices.last().name
        )
    }

    @Test
    fun `when registering user device then it makes log out of other and log in into current`() {
        every { deviceRepository.findAll() } returns devices
        every { deviceRepository.save(any()) } returns devices.first()
        every { deviceRepository.findDeviceById(any()) } returns devices.last()

        deviceServiceImpl.registerUserDevice(user, postDevice)
        assertThat(devices.first().users.first().isLogged, isEqualTo(false))
        assertThat(devices.last().users.first().isLogged, isEqualTo(true))

        verify {
            deviceRepository.findAll()
            deviceRepository.findDeviceById(any())
        }

        verify(exactly = devices.size + 1) {
            deviceRepository.save(any())
        }
    }
}
