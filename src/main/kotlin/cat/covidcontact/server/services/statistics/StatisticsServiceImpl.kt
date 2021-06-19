package cat.covidcontact.server.services.statistics

import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.user.User

class StatisticsServiceImpl(
    private val interactionRepository: InteractionRepository
) : StatisticsService {

    @Synchronized
    override fun getUserInteractionsStatistics(from: Int?, to: Int?): Map<Int, Int> {
        val currentMillis = System.currentTimeMillis()

        val interactions = interactionRepository.findAll()
        println("Interactions: $interactions")

        val filtered = interactions.filterByAge(currentMillis, from) { age, years ->
            age >= years
        }.filterByAge(currentMillis, to) { age, years ->
            age <= years
        }

        val users = filtered.getAllUsers()
        return users.groupBy { user ->
            (currentMillis - user.birthDate).toYears()
        }.toMutableMap().mapValues { (_, userList) -> userList.size }
    }

    private fun getYearIntervalMillis(current: Long, years: Int?) =
        years?.times(DAYS)?.times(HOURS)?.times(SECONDS)?.times(MILLIS)?.let { valueMillis ->
            current - valueMillis
        }

    private fun List<Interaction>.filterByAge(
        currentMillis: Long,
        years: Int?,
        onAge: (Int, Int) -> Boolean
    ) = filter { interaction ->
        years == null || interaction.userInteractions.any { userInteraction ->
            onAge((currentMillis - userInteraction.user.birthDate).toYears(), years)
        }
    }

    private fun List<Interaction>.getAllUsers(): List<User> =
        flatMap { interaction -> interaction.userInteractions }
            .map { userInteraction -> userInteraction.user }

    private fun Long.toYears(): Int = div(MILLIS).div(SECONDS).div(HOURS).div(DAYS).toInt()

    companion object {
        private const val DAYS = 365
        private const val HOURS = 24
        private const val SECONDS = 3600
        private const val MILLIS = 1000
    }
}
