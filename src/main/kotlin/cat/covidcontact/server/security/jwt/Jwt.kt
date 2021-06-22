/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cat.covidcontact.server.security.jwt

import cat.covidcontact.server.security.jwt.algorithms.SignatureAlgorithm
import cat.covidcontact.server.security.jwt.components.Header
import cat.covidcontact.server.security.jwt.components.Payload
import cat.covidcontact.server.security.jwt.components.Signature

class Jwt private constructor(
    val header: Header,
    val payload: Payload,
    private val signature: String,
    private val key: String,
    private val signatureAlgorithm: SignatureAlgorithm,
) {
    val token = "${Encryption.encrypt(header)}.${Encryption.encrypt(payload)}.$signature"
    val isSignatureValid: Boolean
        get() {
            val otherSignature = Signature(
                Encryption.encrypt(header),
                Encryption.encrypt(payload),
                key,
                signatureAlgorithm
            )

            return signature == otherSignature.encrypt()
        }
    val isExpired = payload.exp != null && payload.exp!!.toLong() <= System.currentTimeMillis()

    class Builder(var key: String, var signatureAlgorithm: SignatureAlgorithm) {
        val header = Header()
        val payload = Payload()

        fun build(): Jwt {
            val headerStr = Encryption.encrypt(header)
            val payloadStr = Encryption.encrypt(payload)
            val signature = Signature(headerStr, payloadStr, key, signatureAlgorithm)

            return Jwt(header, payload, signature.encrypt(), key, signatureAlgorithm)
        }
    }

    class Parser(var token: String, var key: String, var signatureAlgorithm: SignatureAlgorithm) {

        fun parse(): Jwt {
            val (headerStr, payloadStr, signatureStr) = token.split(".")
            val header = Encryption.decrypt(headerStr, Header::class.java) as Header
            val payload = Encryption.decrypt(payloadStr, Payload::class.java) as Payload

            return Jwt(header, payload, signatureStr, key, signatureAlgorithm)
        }
    }
}
