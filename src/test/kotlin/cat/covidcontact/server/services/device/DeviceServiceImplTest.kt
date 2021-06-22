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
}
