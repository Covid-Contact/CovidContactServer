package cat.covidcontact.server.services.device

import cat.covidcontact.server.model.nodes.device.Device
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.device.UserDevice
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostDevice

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

        println(deviceNode.users.map { it.user.email })
        deviceRepository.save(deviceNode)
    }
}
