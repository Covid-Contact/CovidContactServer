package cat.covidcontact.server.services.location

data class LocationResponse(
    val country: String?,
    val region: String?,
    val province: String?,
    val city: String?
)
