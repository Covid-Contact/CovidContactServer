package cat.covidcontact.server.controllers.statistics

import cat.covidcontact.server.controllers.runGet
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

    @GetMapping(StatisticsControllerUrls.INTERACTIONS)
    fun getUserStatistics(
        @RequestParam(required = false) from: Int? = null,
        @RequestParam(required = false) to: Int? = null
    ) = runGet {
        val userStatistics = statisticsService.getUserInteractionsStatistics(from, to)
        val (ages, interactions) = userStatistics.toList().unzip()

        PostUserInteractionsStatistics(
            xAxis = ages,
            yAxis = interactions
        )
    }
}
