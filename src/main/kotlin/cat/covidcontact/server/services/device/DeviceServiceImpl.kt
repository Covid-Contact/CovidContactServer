package cat.covidcontact.server.services.device

import cat.covidcontact.server.data.device.Device
import cat.covidcontact.server.data.device.DeviceRepository

class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository
) : DeviceService {

    override fun addDeviceIfNotExists(device: Device) {
        if (!deviceRepository.existsDeviceById(device.id)) {
            deviceRepository.save(device)
        }
    }
}