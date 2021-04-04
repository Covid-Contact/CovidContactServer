package cat.covidcontact.server.services.userdevice

import cat.covidcontact.server.data.device.Device
import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.data.userdevice.UserDevice
import cat.covidcontact.server.data.userdevice.UserDeviceRepository

class UserDeviceServiceImpl(
    private val userDeviceRepository: UserDeviceRepository
) : UserDeviceService {

    override fun registerUserDevice(user: User, device: Device) {
        val userDevices = userDeviceRepository.findAllByUserEmail(user.email)
            .toMutableList()
            .onEach { it.isLogged = false }
        var selectedUserDevice = userDevices.find { it.device.id == device.id }

        if (selectedUserDevice == null) {
            selectedUserDevice = UserDevice(user, device)
            userDevices.add(selectedUserDevice)
        }

        selectedUserDevice.isLogged = true
        userDeviceRepository.saveAll(userDevices)
    }
}
