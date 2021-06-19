package cat.covidcontact.server.model.post

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostUserInteractionsStatistics(
    @JsonProperty("xAxis")
    var xAxis: List<Int>,

    @JsonProperty("yAxis")
    var yAxis: List<Int>
) : Serializable
