package cat.covidcontact.server.services.location.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

data class GeocodingResponse(
    @JsonProperty("results")
    var result: List<ResultResponse>
)
