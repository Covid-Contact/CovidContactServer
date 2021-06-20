package cat.covidcontact.server.services.statistics

import cat.covidcontact.server.model.nodes.user.Gender
import java.util.*

interface StatisticsService {
    fun getUserInteractionsStatistics(from: Int?, to: Int?, gender: Gender?): SortedMap<Int, Int>
    fun getLocationInteractionsStatistics(
        country: String?,
        region: String?,
        province: String?
    ): SortedMap<String, Int>
}
