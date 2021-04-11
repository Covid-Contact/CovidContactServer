package cat.covidcontact.server.post

import cat.covidcontact.server.data.user.Gender
import cat.covidcontact.server.data.user.Marriage
import cat.covidcontact.server.data.user.Occupation
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PostUser(
    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    var username: String,

    @SerializedName("gender")
    val gender: Gender,

    @SerializedName("birth_date")
    val birthDate: Long,

    @SerializedName("city")
    var city: String? = null,

    @SerializedName("studies")
    var studies: String? = null,

    @SerializedName("occupation")
    var occupation: Occupation? = null,

    @SerializedName("marriage")
    var marriage: Marriage? = null,

    @SerializedName("children")
    var children: Int? = null,

    @SerializedName("has_been_positive")
    var hasBeenPositive: Boolean? = null,

    @SerializedName("is_vaccinated")
    var isVaccinated: Boolean? = null
) : Serializable
