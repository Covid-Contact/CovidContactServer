package cat.covidcontact.server.services.device

import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.post.PostDevice

interface DeviceService {
    fun registerUserDevice(user: User, device: PostDevice)
}
