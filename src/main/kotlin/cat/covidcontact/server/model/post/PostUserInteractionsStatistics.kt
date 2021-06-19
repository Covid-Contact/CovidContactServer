package cat.covidcontact.server.model.post

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostUserInteractionsStatistics(
    @JsonProperty("xAxes")
    var xAxes: List<Any>,

    @JsonProperty("yAxes")
    var yAxes: List<Any>
) : Serializable
