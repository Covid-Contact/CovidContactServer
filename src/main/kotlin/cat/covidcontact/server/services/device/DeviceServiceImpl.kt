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

import cat.covidcontact.server.model.nodes.device.Device
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.device.UserDevice
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostDevice
import org.springframework.stereotype.Service

@Service
class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository
) : DeviceService {

    @Synchronized
    override fun registerUserDevice(user: User, device: PostDevice) {
        logoutOtherDevices(user)
        loginCurrentDevice(device, user)
    }

    private fun logoutOtherDevices(user: User) {
        val devices = deviceRepository.findAll()
        devices.forEach { device ->
            val userDevice = device.users.find { it.user.email == user.email }
            userDevice?.let { userDev ->
                userDev.isLogged = false
                deviceRepository.save(device)
            }
        }
    }

    private fun loginCurrentDevice(device: PostDevice, user: User) {
        val deviceNode = deviceRepository.findDeviceById(device.id)?.let { currentDevice ->
            currentDevice.also { dev ->
                dev.users.forEach { it.isLogged = false }
                dev.users.find { it.user.email == user.email }?.apply {
                    isLogged = true
                } ?: dev.users.add(UserDevice(user = user, isLogged = true))
            }
        } ?: Device(
            device.id, device.name, mutableListOf(
                UserDevice(user = user, isLogged = true)
            )
        )

        deviceRepository.save(deviceNode)
    }
}
