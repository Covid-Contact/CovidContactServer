package cat.covidcontact.server.data.device

import com.google.gson.annotations.SerializedName
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node
data class Device(
    @SerializedName("id")
    @Id
    val id: String,

    @SerializedName("name")
    val name: String
)
