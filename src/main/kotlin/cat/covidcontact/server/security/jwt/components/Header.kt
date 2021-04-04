package cat.covidcontact.server.security.jwt.components

import cat.covidcontact.server.security.jwt.algorithms.Algorithm

class Header {
    var alg: Algorithm? = null
    var typ: String = "JWT"
}
