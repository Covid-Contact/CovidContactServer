package cat.covidcontact.server.services.location.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

data class AddressComponentsResponse(
    @JsonProperty("long_name")
    var longName: String,

    @JsonProperty("short_name")
    var shortName: String,

    @JsonProperty("types")
    var types: List<String>
)
