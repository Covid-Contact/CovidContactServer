package cat.covidcontact.server.controllers.statistics

object StatisticsControllerUrls {
    const val BASE = "/statistics"
    private const val INTERACTIONS = "interactions"
    private const val DURATIONS = "durations"
    private const val USER = "/user"
    const val USER_INTERACTIONS = "$USER/$INTERACTIONS"
    private const val LOCATION = "/location"
    const val LOCATION_INTERACTIONS = "$LOCATION/$INTERACTIONS"
}
