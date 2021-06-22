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
