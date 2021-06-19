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
        val fromMillis = getYearIntervalMillis(currentMillis, from)
        val toMillis = getYearIntervalMillis(currentMillis, to)

        val interactions = interactionRepository.findAll().toList()
        val filtered = interactions.filterByBirthdate(fromMillis) { birthDate, yearsMillis ->
            birthDate >= yearsMillis
        }.filterByBirthdate(toMillis) { birthDate, yearsMillis ->
            birthDate <= yearsMillis
        }

        val users = filtered.getAllUsers()
        return users.groupBy { user ->
            user.birthDate.toYears()
        }.toMutableMap().mapValues { (_, users) -> users.size }
    }

    private fun getYearIntervalMillis(current: Long, years: Int?) =
        years?.times(DAYS)?.times(HOURS)?.times(SECONDS)?.times(MILLIS)?.let { valueMillis ->
            current - valueMillis
        }

    private fun List<Interaction>.filterByBirthdate(
        yearsMillis: Long?,
        onBirthDate: (Long, Long) -> Boolean
    ) = filter { interaction ->
        yearsMillis == null || interaction.userInteractions.any { userInteraction ->
            onBirthDate(userInteraction.user.birthDate, yearsMillis)
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
