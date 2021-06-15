package cat.covidcontact.server.services.interaction.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

data class GeocodingResponse(
    @JsonProperty("results")
    var result: List<ResultResponse>
)
