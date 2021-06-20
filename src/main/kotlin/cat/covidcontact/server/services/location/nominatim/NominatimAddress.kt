package cat.covidcontact.server.services.location.nominatim

import com.fasterxml.jackson.annotation.JsonProperty

data class NominatimAddress(
    @JsonProperty("city")
    var city: String? = null,

    @JsonProperty("town")
    var town: String? = null,

    @JsonProperty("village")
    var village: String? = null,

    @JsonProperty("county")
    var county: String? = null,

    @JsonProperty("municipality")
    var municipality: String? = null,

    @JsonProperty("state")
    var state: String? = null,

    @JsonProperty("country")
    var country: String? = null
)
