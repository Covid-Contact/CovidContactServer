package cat.covidcontact.server.security

import cat.covidcontact.server.controllers.UserControllerUrls
import cat.covidcontact.server.services.UserDetailServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
class WebSecurity(
    private val userDetailsService: UserDetailServiceImpl,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        val authenticationFilter = AuthenticationFilter(authenticationManager()).apply {
            setFilterProcessesUrl(UserControllerUrls.BASE + UserControllerUrls.LOG_IN)
        }

        val authorizationFilter = AuthorizationFilter(authenticationManager())

        http.cors().and().csrf().disable().authorizeRequests()
            .antMatchers(
                HttpMethod.POST,
                UserControllerUrls.BASE + UserControllerUrls.SIGN_UP,
                UserControllerUrls.BASE + UserControllerUrls.LOG_IN,
                UserControllerUrls.BASE + UserControllerUrls.VALIDATE
            )
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(authenticationFilter)
            .addFilter(authorizationFilter)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())

        return source
    }
}