package cat.covidcontact.server.controllers.contactnetwork

object ContactNetworkControllerUrls {
    const val BASE = "/contactnetwork"
    const val CREATE_CONTACT_NETWORK = "/"
    const val GET_CONTACT_NETWORKS = "/"
    const val ENABLE_USER_ADDITION = "/{name}"
    const val GENERATE_ACCESS_CODE = "/{name}/generateAccessCode"
    const val GET_CONTACT_NETWORK_BY_ACCESS_CODE = "/accessCode"
    const val JOIN_CONTACT_NETWORK = "/{name}/join"
    const val EXIT_CONTACT_NETWORK = "/{name}/exit"
}
