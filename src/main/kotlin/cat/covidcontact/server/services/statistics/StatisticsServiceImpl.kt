package cat.covidcontact.server.services.statistics

import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.location.CountryRepository
import cat.covidcontact.server.model.nodes.location.ProvinceRepository
import cat.covidcontact.server.model.nodes.location.RegionRepository
import cat.covidcontact.server.model.nodes.user.Gender
import cat.covidcontact.server.model.nodes.user.User
import java.util.*

class StatisticsServiceImpl(
    private val interactionRepository: InteractionRepository,
    private val countryRepository: CountryRepository,
    private val regionRepository: RegionRepository,
    private val provinceRepository: ProvinceRepository,
) : StatisticsService {

    @Synchronized
    override fun getUserInteractionsStatistics(
        from: Int?,
        to: Int?,
        gender: Gender?
    ): SortedMap<Int, Int> {
        val currentMillis = System.currentTimeMillis()
        val interactions = interactionRepository.findAll()
        val filtered = interactions.filterByAge(currentMillis, from) { age, years ->
            age >= years
        }.filterByAge(currentMillis, to) { age, years ->
            age <= years
        }

        val users = filtered.getAllUsers().filter { user ->
            gender == null || user.gender == gender
        }

        return users.groupBy { user ->
            (currentMillis - user.birthDate).toYears()
        }.toMutableMap().mapValues { (_, userList) -> userList.size }.toSortedMap()
    }

    @Synchronized
    override fun getLocationInteractionsStatistics(
        country: String?,
        region: String?,
        province: String?,
    ): SortedMap<String, Int> {
        val interactions = interactionRepository.findAll().filter { interaction ->
            interaction.cities != null
        }
        return when {
            country != null -> getInteractionsFromCountry(interactions, country)
            region != null -> getInteractionsFromRegion(interactions, region)
            province != null -> getInteractionsFromProvince(interactions, province)
            else -> emptyMap()
        }.toSortedMap()
    }

    private fun getInteractionsFromCountry(
        interactions: List<Interaction>,
        name: String
    ): Map<String, Int> {
        return countryRepository.findCountryByName(name)?.let { country ->
            val cities = country.regions.flatMap { region -> region.provinces }
                .flatMap { province -> province.cities }.map { city -> city.name }
            getLocationInteractionStatistics(interactions, cities)
        } ?: emptyMap()
    }

    private fun getInteractionsFromRegion(
        interactions: List<Interaction>,
        name: String
    ): Map<String, Int> {
        return regionRepository.findRegionByName(name)?.let { region ->
            val cities = region.provinces.flatMap { province -> province.cities }
                .map { city -> city.name }
            getLocationInteractionStatistics(interactions, cities)
        } ?: emptyMap()
    }

    private fun getInteractionsFromProvince(
        interactions: List<Interaction>,
        name: String
    ): Map<String, Int> {
        return provinceRepository.findProvinceByName(name)?.let { province ->
            val cities = province.cities.map { city -> city.name }
            getLocationInteractionStatistics(interactions, cities)
        } ?: emptyMap()
    }

    private fun getLocationInteractionStatistics(
        interactions: List<Interaction>,
        cities: List<String>
    ): Map<String, Int> {
        val interactionCities = interactions.flatMap { interaction ->
            interaction.cities!!
        }.toSet().map { city -> city.name }

        val citiesWithInteractions = cities intersect interactionCities
        return citiesWithInteractions.sorted().associateWith { cityName ->
            interactions.getAmountAtCity(cityName)
        }
    }

    private fun List<Interaction>.getAmountAtCity(cityName: String): Int =
        filter { interaction ->
            interaction.cities?.find { city -> city.name == cityName } != null
        }.size

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
