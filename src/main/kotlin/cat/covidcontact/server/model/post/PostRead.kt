package cat.covidcontact.server.model.post

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostRead(
    @JsonProperty("current_device_id")
    var currentDeviceId: String,

    @JsonProperty("device_ids")
    var deviceIds: List<String>,

    @JsonProperty("date_time")
    var dateTime: Long,

    @JsonProperty("lat")
    var lat: Double? = null,

    @JsonProperty("lon")
    var lon: Double? = null
) : Serializable
