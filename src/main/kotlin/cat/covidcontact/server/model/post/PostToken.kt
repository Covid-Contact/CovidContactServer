package cat.covidcontact.server.model.post

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostToken(
    @JsonProperty("email")
    var email: String,

    @JsonProperty("token")
    var token: String
) : Serializable
