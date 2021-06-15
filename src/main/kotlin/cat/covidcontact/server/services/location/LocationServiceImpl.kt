package cat.covidcontact.server.services.location

import cat.covidcontact.server.services.interaction.geocoding.AddressComponentsResponse
import cat.covidcontact.server.services.interaction.geocoding.GeocodingResponse
import org.springframework.boot.web.client.RestTemplateBuilder

class LocationServiceImpl : LocationService {
    private val restTemplate = RestTemplateBuilder().build()
    private val baseUrl = "https://maps.googleapis.com/maps/api/geocode/json"
    private val geocodingKey = System.getenv("COVID_CONTACT_MAPS_KEY")
    private val language = "en"

    override fun getLocationFromCoordinates(lat: Double, lon: Double): LocationResponse {
        val latLng = "$lat,$lon"
        val url = "$baseUrl?latlng=$latLng&key=$geocodingKey&language=$language"
        return parseLocationResponse(url)
    }

    override fun getLocationFromName(name: String): LocationResponse {
        val url = "$baseUrl?address=$name&key=$geocodingKey&language=$language"
        return parseLocationResponse(url)
    }

    private fun parseLocationResponse(url: String): LocationResponse {
        val response = restTemplate.getForObject(url, GeocodingResponse::class.java)
        val addressComponents = response?.result?.first()?.addressComponents

        val countryName = addressComponents?.findComponent(COUNTRY)
        val regionName = addressComponents?.findComponent(REGION)
        val provinceName = addressComponents?.findComponent(PROVINCE)
        val cityName = addressComponents?.findComponent(CITY)

        return LocationResponse(countryName, regionName, provinceName, cityName)
    }

    private fun List<AddressComponentsResponse>.findComponent(type: String): String? =
        find { component -> component.types.contains(type) }?.longName

    companion object {
        private const val COUNTRY = "country"
        private const val REGION = "administrative_area_level_1"
        private const val PROVINCE = "administrative_area_level_2"
        private const val CITY = "locality"
    }
}
