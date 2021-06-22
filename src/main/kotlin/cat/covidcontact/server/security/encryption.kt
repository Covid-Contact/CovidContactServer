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

package cat.covidcontact.server.security

import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val IV_SIZE = 16
private val key = System.getenv("COVID_CONTACT_AES_KEY")!!

fun String.encrypt(): String {
    val keyBytes = key.toBytes()
    val secretKeySpec = SecretKeySpec(keyBytes, "AES")

    val ivBytes = ByteArray(IV_SIZE)
    SecureRandom().nextBytes(ivBytes)
    val ivParameterSpec = IvParameterSpec(ivBytes)

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

    val encrypted = cipher.doFinal(toByteArray())
    return Base64.getEncoder().encodeToString(ivBytes + encrypted)
}

fun String.decrypt(): String {
    val encrypted = Base64.getDecoder().decode(this)
    val keyBytes = key.toBytes()

    val secretKeySpec = SecretKeySpec(keyBytes, "AES")
    val ivParameterSpec = IvParameterSpec(encrypted.take(IV_SIZE).toByteArray())

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

    val decrypted = cipher.doFinal(encrypted.drop(IV_SIZE).toByteArray())
    return String(decrypted)
}

private fun String.toBytes(): ByteArray = chunked(2)
    .map { byte -> byte.toInt(16).toByte() }
    .toByteArray()
