package cat.covidcontact.server.services.location.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

data class ResultResponse(
    @JsonProperty("address_components")
    var addressComponents: List<AddressComponentsResponse>
)
