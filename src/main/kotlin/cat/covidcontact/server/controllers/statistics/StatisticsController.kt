/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
            xAxes = xAxis,
            yAxes = yAxis
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
            xAxes = xAxis,
            yAxes = yAxis
        )
    }
}
