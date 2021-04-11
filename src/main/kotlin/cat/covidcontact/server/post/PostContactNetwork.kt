package cat.covidcontact.server.post

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostContactNetwork(
    @JsonProperty("name")
    var name: String,

    @JsonProperty("password")
    var password: String? = null,

    @JsonProperty("owner_email")
    var ownerEmail: String? = null,

    @JsonProperty("owner_username")
    var ownerUsername: String? = null,

    @JsonProperty("members_username")
    val membersUsername: List<String>? = null
) : Serializable
