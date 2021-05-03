package cat.covidcontact.server.services.device

import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.post.PostDevice

interface DeviceService {
    fun registerUserDevice(user: User, device: PostDevice)
}
