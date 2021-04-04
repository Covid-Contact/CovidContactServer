package cat.covidcontact.server.data.user

import com.google.gson.annotations.SerializedName
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import java.io.Serializable

@Node
data class User(
    @SerializedName("email")
    @Id
    val email: String,

    @SerializedName("username")
    val username: String,

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
