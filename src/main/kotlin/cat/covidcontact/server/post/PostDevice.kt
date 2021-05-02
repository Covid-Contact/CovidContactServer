package cat.covidcontact.server.post

import com.fasterxml.jackson.annotation.JsonProperty

data class PostDevice(
    @JsonProperty("id")
    var id: String,

    @JsonProperty("name")
    var name: String
)
