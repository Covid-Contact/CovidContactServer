package cat.covidcontact.server.services.userdevice

import cat.covidcontact.server.data.device.Device
import cat.covidcontact.server.data.user.User

interface UserDeviceService {
    fun registerUserDevice(user: User, device: Device)
}
