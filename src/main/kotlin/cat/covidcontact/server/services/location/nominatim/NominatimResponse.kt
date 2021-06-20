package cat.covidcontact.server.services.location.nominatim

import com.fasterxml.jackson.annotation.JsonProperty

data class NominatimResponse(
    @JsonProperty("address")
    var address: NominatimAddress
)
