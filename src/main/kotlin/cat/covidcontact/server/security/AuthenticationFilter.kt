package cat.covidcontact.server.security

import cat.covidcontact.server.data.ApplicationUser
import cat.covidcontact.server.security.jwt.Jwt
import cat.covidcontact.server.security.jwt.algorithms.Algorithm
import cat.covidcontact.server.security.jwt.algorithms.SignatureAlgorithm
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFilter(
    private val authManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {
        val credentials = ObjectMapper().readValue(request.inputStream, ApplicationUser::class.java)
        return authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                credentials.email,
                credentials.password,
                emptyList()
            )
        )
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val subject = (authResult?.principal as User).username
        val expirationDate = System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME
        val builder = Jwt.Builder(
            key = SecurityConstants.PRIVATE_KEY,
            signatureAlgorithm = SignatureAlgorithm.HmacSHA512
        ).apply {
            header.alg = Algorithm.HS512
            payload.sub = subject
            payload.exp = expirationDate.toString()
        }

        val jwt = builder.build()
        response?.addHeader(
            SecurityConstants.HEADER_STRING,
            SecurityConstants.TOKEN_PREFIX + jwt.token
        )
    }
}