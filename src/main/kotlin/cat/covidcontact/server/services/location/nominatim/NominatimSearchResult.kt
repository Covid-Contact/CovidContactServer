package cat.covidcontact.server.services.location.nominatim

import com.fasterxml.jackson.annotation.JsonProperty

data class NominatimSearchResult(
    @JsonProperty("lat")
    var lat: String? = null,

    @JsonProperty("lon")
    var lon: String? = null,

    @JsonProperty("type")
    var type: String? = null
)
