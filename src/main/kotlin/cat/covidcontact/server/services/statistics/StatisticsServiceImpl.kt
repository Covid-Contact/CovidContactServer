package cat.covidcontact.server.services.statistics

import cat.covidcontact.server.model.nodes.interaction.Interaction
import cat.covidcontact.server.model.nodes.interaction.InteractionRepository
import cat.covidcontact.server.model.nodes.interaction.UserInteraction
import cat.covidcontact.server.model.nodes.location.*
import cat.covidcontact.server.model.nodes.user.Gender
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

        val ages = interactions.flatMap { interaction -> interaction.userInteractions }
            .map { userInteraction -> userInteraction.calculateAge(currentMillis) }
            .toSet()

        val filteredAges = ages.filter { age -> from == null || age >= from }
            .filter { age -> to == null || age <= to }

        return filteredAges.associateWith { age ->
            interactions.getAmountWithAge(age, gender, currentMillis)
        }.toSortedMap()
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
            country.regions.associate { region ->
                region.name to getInteractionsFromCurrentRegion(interactions, region)
            }.mapValues { (_, region) -> region.values.sum() }
        } ?: emptyMap()
    }

    private fun getInteractionsFromRegion(
        interactions: List<Interaction>,
        name: String
    ): Map<String, Int> {
        return regionRepository.findRegionByName(name)?.let { region ->
            getInteractionsFromCurrentRegion(interactions, region)
        } ?: emptyMap()
    }

    private fun getInteractionsFromProvince(
        interactions: List<Interaction>,
        name: String
    ): Map<String, Int> {
        return provinceRepository.findProvinceByName(name)?.let { province ->
            getInteractionsFromCurrentProvince(interactions, province)
        } ?: emptyMap()
    }

    private fun getInteractionsFromCurrentRegion(
        interactions: List<Interaction>,
        region: Region
    ): Map<String, Int> {
        return region.provinces.associate { province ->
            province.name to getInteractionsFromCurrentProvince(interactions, province)
        }.mapValues { (_, province) -> province.values.sum() }
    }

    private fun getInteractionsFromCurrentProvince(
        interactions: List<Interaction>,
        province: Province
    ): Map<String, Int> {
        val cityNames = province.getCityNames()
        val interactionCities = interactions.getCityNames()
        val citiesWithInteractions = cityNames intersect interactionCities

        return citiesWithInteractions.sorted().associateWith { cityName ->
            interactions.getAmountAtCity(cityName)
        }
    }

    private fun UserInteraction.calculateAge(currentMillis: Long): Int =
        (currentMillis - user.birthDate).toYears()

    private fun Province.getCityNames(): List<String> =
        cities.map { city -> city.name }

    private fun List<Interaction>.getCityNames(): List<String> =
        flatMap { interaction ->
            interaction.cities!!
        }.toSet().map { city -> city.name }

    private fun List<Interaction>.getAmountWithAge(
        age: Int,
        gender: Gender?,
        currentMillis: Long
    ): Int =
        filter { interaction ->
            interaction.userInteractions.find { userInteraction ->
                (gender == null || userInteraction.user.gender == gender) &&
                    userInteraction.calculateAge(currentMillis) == age
            } != null
        }.size

    private fun List<Interaction>.getAmountAtCity(cityName: String): Int =
        filter { interaction ->
            interaction.cities?.find { city -> city.name == cityName } != null
        }.size

    private fun Long.toYears(): Int = div(MILLIS).div(SECONDS).div(HOURS).div(DAYS).toInt()

    companion object {
        private const val DAYS = 365
        private const val HOURS = 24
        private const val SECONDS = 3600
        private const val MILLIS = 1000
    }
}
