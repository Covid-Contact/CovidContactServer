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

import com.google.gson.Gson
import java.nio.charset.Charset
import java.util.*

object Encryption {
    fun encrypt(src: Any): String {
        val json = Gson().toJson(src)
        return Base64.getUrlEncoder().encodeToString(json.toByteArray())
    }

    fun decrypt(src: String, cls: Class<*>): Any {
        val decrypted = Base64.getUrlDecoder().decode(src).toString(Charset.forName("UTF-8"))
        return Gson().fromJson(decrypted, cls)
    }
}
