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

import cat.covidcontact.server.controllers.statistics.StatisticsControllerUrls
import cat.covidcontact.server.controllers.user.UserControllerUrls
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
                UserControllerUrls.BASE + UserControllerUrls.LOG_IN
            )
            .permitAll()
            .antMatchers(
                HttpMethod.GET,
                UserControllerUrls.BASE + UserControllerUrls.VALIDATE,
                UserControllerUrls.BASE + UserControllerUrls.VALIDATED,
                StatisticsControllerUrls.BASE + StatisticsControllerUrls.USER_INTERACTIONS,
                StatisticsControllerUrls.BASE + StatisticsControllerUrls.LOCATION_INTERACTIONS
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
