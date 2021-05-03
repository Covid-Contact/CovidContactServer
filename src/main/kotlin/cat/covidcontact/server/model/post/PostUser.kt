package cat.covidcontact.server.model.post

import cat.covidcontact.server.model.nodes.user.Gender
import cat.covidcontact.server.model.nodes.user.Marriage
import cat.covidcontact.server.model.nodes.user.Occupation
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PostUser(
    @JsonProperty("email")
    val email: String,

    @JsonProperty("username")
    var username: String,

    @JsonProperty("gender")
    val gender: Gender,

    @JsonProperty("birth_date")
    val birthDate: Long,

    @JsonProperty("city")
    var city: String? = null,

    @JsonProperty("studies")
    var studies: String? = null,

    @JsonProperty("occupation")
    var occupation: Occupation? = null,

    @JsonProperty("marriage")
    var marriage: Marriage? = null,

    @JsonProperty("children")
    var children: Int? = null,

    @JsonProperty("has_been_positive")
    var hasBeenPositive: Boolean? = null,

    @JsonProperty("is_vaccinated")
    var isVaccinated: Boolean? = null
) : Serializable
