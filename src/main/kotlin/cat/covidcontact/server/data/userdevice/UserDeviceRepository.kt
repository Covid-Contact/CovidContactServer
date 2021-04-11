package cat.covidcontact.server.data.userdevice

import cat.covidcontact.server.data.device.Device
import cat.covidcontact.server.data.user.User
import org.springframework.data.neo4j.repository.Neo4jRepository

interface UserDeviceRepository : Neo4jRepository<UserDevice, Long> {
    fun findUserDeviceByUserAndDevice(user: User, device: Device): UserDevice?

    fun findAllByUserEmail(email: String): List<UserDevice>
}
