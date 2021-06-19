package cat.covidcontact.server.services.statistics

import cat.covidcontact.server.model.nodes.user.Gender

interface StatisticsService {

    fun getUserInteractionsStatistics(from: Int?, to: Int?, gender: Gender?): Map<Int, Int>
}
