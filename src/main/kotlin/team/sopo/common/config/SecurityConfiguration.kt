package team.sopo.common.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import team.sopo.common.config.auth.CustomAccessDeniedHandler
import team.sopo.common.config.auth.CustomAuthenticationEntryPoint
import team.sopo.common.config.jwt.JwtAuthenticationFilter
import team.sopo.common.config.jwt.JwtProvider
import team.sopo.common.consts.Role

@Configuration
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration(
    private val jwtProvider: JwtProvider,
    private val accessDeniedHandler: CustomAccessDeniedHandler,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint
) : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var projectConfiguration: ProjectConfig

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/favicon.ico",
            "/error",
            "/swagger-resources/**",
            "/**/swagger-ui/**",
            "/webjars/**",
            "/sopo-parcel/api-docs",
            "/**/asset/**",
            "/**/swagger-ui.html",
            "/**/internal/**"
        )
    }

    override fun configure(http: HttpSecurity) {

        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .cors()
            .and()
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .authorizeRequests()
            .antMatchers(projectConfiguration.adminUserAntMatchers).hasRole(Role.ADMIN)
            .antMatchers("${projectConfiguration.apiPath}/**")
            .fullyAuthenticated()
            .and()
            .addFilterAfter(JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(authenticationEntryPoint)
    }
}