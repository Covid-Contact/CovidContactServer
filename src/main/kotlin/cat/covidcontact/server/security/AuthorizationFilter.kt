package cat.covidcontact.server.security

import cat.covidcontact.server.security.jwt.Jwt
import cat.covidcontact.server.security.jwt.algorithms.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter(
    private val authManager: AuthenticationManager
) : BasicAuthenticationFilter(authManager) {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val header: String? = request.getHeader(SecurityConstants.HEADER_STRING)

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response)
            return
        }

        val authentication = getAuthentication(request)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }

    private fun getAuthentication(
        request: HttpServletRequest
    ): UsernamePasswordAuthenticationToken? {
        val token: String? = request.getHeader(SecurityConstants.HEADER_STRING)
        return token?.let {
            val parser = Jwt.Parser(
                token = it,
                key = SecurityConstants.PRIVATE_KEY,
                signatureAlgorithm = SignatureAlgorithm.HmacSHA512
            )

            val jwt = parser.parse()
            if (jwt.isSignatureValid && !jwt.isExpired) {
                UsernamePasswordAuthenticationToken(jwt.payload.sub, null, emptyList())
            } else {
                null
            }
        }
    }
}