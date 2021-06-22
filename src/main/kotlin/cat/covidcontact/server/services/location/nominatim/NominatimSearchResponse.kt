package cat.covidcontact.server.services.location.nominatim

data class NominatimSearchResponse(
    var results: List<NominatimSearchResult>
)
