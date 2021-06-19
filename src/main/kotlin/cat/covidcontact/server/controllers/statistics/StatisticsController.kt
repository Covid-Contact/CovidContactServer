package cat.covidcontact.server.controllers.statistics

import cat.covidcontact.server.controllers.runGet
import cat.covidcontact.server.model.nodes.user.Gender
import cat.covidcontact.server.model.post.PostUserInteractionsStatistics
import cat.covidcontact.server.services.statistics.StatisticsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(StatisticsControllerUrls.BASE)
class StatisticsController(
    private val statisticsService: StatisticsService
) {

    @GetMapping(StatisticsControllerUrls.USER_INTERACTIONS)
    fun getUserInteractionsStatistics(
        @RequestParam(required = false) from: Int? = null,
        @RequestParam(required = false) to: Int? = null,
        @RequestParam(required = false) gender: Gender? = null
    ) = runGet {
        val userStatistics = statisticsService.getUserInteractionsStatistics(from, to, gender)
        val (xAxis, yAxis) = userStatistics.toList().unzip()

        PostUserInteractionsStatistics(
            xAxis = xAxis,
            yAxis = yAxis
        )
    }

    @GetMapping(StatisticsControllerUrls.LOCATION_INTERACTIONS)
    fun getLocationInteractionsStatistics(
        @RequestParam(required = false) country: String? = null,
        @RequestParam(required = false) region: String? = null,
        @RequestParam(required = false) province: String? = null
    ) = runGet {
        val userStatistics =
            statisticsService.getLocationInteractionsStatistics(country, region, province)
        val (xAxis, yAxis) = userStatistics.toList().unzip()

        PostUserInteractionsStatistics(
            xAxis = xAxis,
            yAxis = yAxis
        )
    }
}
