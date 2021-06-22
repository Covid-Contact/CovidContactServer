/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cat.covidcontact.server.services.location

import cat.covidcontact.server.services.location.nominatim.NominatimAddress
import cat.covidcontact.server.services.location.nominatim.NominatimResponse
import cat.covidcontact.server.services.location.nominatim.NominatimSearchResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class LocationServiceImpl : LocationService {
    private val restTemplate = RestTemplateBuilder().build()

    private val nominatimBaseUrl = "https://nominatim.openstreetmap.org"
    private val nominatimSearchUrl = "$nominatimBaseUrl/search.php"
    private val nominatimReverseUrl = "$nominatimBaseUrl/reverse.php"

    private val accents = mapOf(
        "à" to "a", "á" to "a", "â" to "a",
        "è" to "e", "é" to "e", "ê" to "e",
        "ì" to "i", "í" to "i", "î" to "i",
        "ò" to "o", "ó" to "o", "ô" to "o",
        "ù" to "u", "ú" to "u", "û" to "u"
    )

    override fun getLocationFromCoordinates(lat: Double, lon: Double): LocationResponse {
        val url = "$nominatimReverseUrl?lat=$lat&lon=$lon&format=jsonv2"
        return parseNominatimLocationResponse(url)
    }

    override fun getLocationFromName(name: String): LocationResponse {
        val encodedName = URLEncoder.encode(name.removeAccents(), StandardCharsets.UTF_8)
        val url = "$nominatimSearchUrl?q=$encodedName&format=jsonv2"
        println(name.removeAccents())
        val response = restTemplate.getForObject(url, String::class.java)!!
        val result = Gson().fromJson<List<NominatimSearchResult>>(response).first { actualResult ->
            actualResult.type == "administrative"
        }

        return if (result.lat != null && result.lon != null) {
            getLocationFromCoordinates(result.lat!!.toDouble(), result.lon!!.toDouble())
        } else {
            LocationResponse(null, null, null, null)
        }
    }

    private fun parseNominatimLocationResponse(url: String): LocationResponse {
        val response = restTemplate.getForObject(url, NominatimResponse::class.java)
        val address = response?.address

        return LocationResponse(
            country = address?.country,
            region = address?.state,
            province = getProvince(address),
            city = getCity(address)
        )
    }

    private fun getProvince(address: NominatimAddress?): String? {
        return address?.let { currentAddress ->
            if (currentAddress.county != null) {
                currentAddress.county!!.split(" ").first()
            } else {
                currentAddress.municipality
            }
        }
    }

    private fun getCity(address: NominatimAddress?): String? {
        return address?.let { currentAddress ->
            when {
                currentAddress.city != null -> currentAddress.city
                currentAddress.town != null -> currentAddress.town
                else -> currentAddress.village
            }
        }
    }

    private inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

    private fun String.removeAccents(): String {
        var result = this
        accents.forEach { (accent, withoutAccent) ->
            result = result.replace(accent, withoutAccent)
                .replace(
                    accent.toUpperCase(Locale.getDefault()),
                    withoutAccent.toUpperCase(Locale.getDefault())
                )
        }

        return result.replace("'", "")
    }
}
