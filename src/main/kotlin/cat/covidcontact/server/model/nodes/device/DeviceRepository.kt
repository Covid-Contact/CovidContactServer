package cat.covidcontact.server.model.nodes.device

import org.springframework.data.neo4j.repository.Neo4jRepository

interface DeviceRepository : Neo4jRepository<Device, String> {
    fun findDeviceById(id: String): Device?
}
