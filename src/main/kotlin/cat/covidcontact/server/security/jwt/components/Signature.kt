package cat.covidcontact.server.security.jwt.components

import cat.covidcontact.server.security.jwt.algorithms.SignatureAlgorithm
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Signature(
    private val headerEncrypted: String,
    private val payloadEncrypted: String,
    private val key: String,
    private val signatureAlgorithm: SignatureAlgorithm
) {

    fun encrypt(): String {
        val input = "$headerEncrypted.$payloadEncrypted"
        val keySpec = SecretKeySpec(key.toByteArray(), signatureAlgorithm.toString())
        val mac = Mac.getInstance(signatureAlgorithm.toString())
        mac.init(keySpec)

        val bytes = mac.doFinal(input.toByteArray())
        return Base64.getUrlEncoder().encodeToString(bytes)
    }
}