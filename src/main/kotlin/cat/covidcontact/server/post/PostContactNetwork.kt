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

    @JsonProperty("is_visible")
    val isVisible: Boolean = true,

    @JsonProperty("is_password_protected")
    val isPasswordProtected: Boolean = true

) : Serializable
