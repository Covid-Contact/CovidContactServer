package cat.covidcontact.server.model.post

import com.fasterxml.jackson.annotation.JsonProperty

data class PostNotificationKey(
    @JsonProperty("notification_key")
    val notificationKey: String
)
