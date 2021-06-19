package cat.covidcontact.server.services.statistics

interface StatisticsService {

    fun getUserInteractionsStatistics(from: Int?, to: Int?): Map<Int, Int>
}
