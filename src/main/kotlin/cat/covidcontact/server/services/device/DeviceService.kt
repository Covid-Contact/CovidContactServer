package cat.covidcontact.server.services.device

import cat.covidcontact.server.data.device.Device

interface DeviceService {
    fun addDeviceIfNotExists(device: Device)
}
