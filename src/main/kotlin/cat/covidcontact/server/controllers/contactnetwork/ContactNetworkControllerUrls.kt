package cat.covidcontact.server.controllers.contactnetwork

object ContactNetworkControllerUrls {
    const val BASE = "/contactnetwork"
    const val CREATE_CONTACT_NETWORK = "/"
    const val GET_CONTACT_NETWORKS = "/"
    const val ENABLE_USER_ADDITION = "/{name}"
    const val GENERATE_ACCESS_CODE = "/{name}/generateAccessCode"
    const val GET_CONTACT_NETWORK_BY_ACCESS_CODE = "/accessCode"
}