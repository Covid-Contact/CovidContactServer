package cat.covidcontact.server.services.interaction.geocoding

import com.fasterxml.jackson.annotation.JsonProperty

data class ResultResponse(
    @JsonProperty("address_components")
    var addressComponents: List<AddressComponentsResponse>
)
