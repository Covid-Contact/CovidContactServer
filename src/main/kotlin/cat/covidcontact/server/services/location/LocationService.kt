package cat.covidcontact.server.services.location

interface LocationService {
    fun getLocationFromCoordinates(lat: Double, lon: Double): LocationResponse

    fun getLocationFromName(name: String): LocationResponse
}
