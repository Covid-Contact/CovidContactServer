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