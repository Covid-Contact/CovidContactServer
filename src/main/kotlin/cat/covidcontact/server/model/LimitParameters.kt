package cat.covidcontact.server.model

object LimitParameters {
    const val MIN_CONTAGIOUS_DURATION = 5 * 60 * 1000
    const val CONTAGIOUS_PERIOD = 14 * 24 * 3600 * 1000
    const val MAX_PEOPLE_RECOMMENDED = 6
}
