package team.sopo.common.config

import team.sopo.common.consts.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.SecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler

@Configuration
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration: WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var projectConfig: ProjectConfig

    @Autowired
    lateinit var roleHierarchy: RoleHierarchy

    private fun webExpressionHandler(): SecurityExpressionHandler<FilterInvocation?>? {
        val defaultWebSecurityExpressionHandler = DefaultWebSecurityExpressionHandler()
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy)
        return defaultWebSecurityExpressionHandler
    }

    override fun configure(web: WebSecurity?) {
        web?.
        ignoring()?.
        antMatchers(
                "/api/v1/sopo-api/join/**",
                "/api/v1/sopo-api/validation/email/exist/{email}",
                "/**/asset/**",
                "/**/v2/**",
                "/**/configuration/ui",
                "/**/swagger-resources/**",
                "/**/configuration/security",
                "/**/swagger-ui.html",
                "/**/webjars/**",
                "/**/apple-touch-icon-precomposed.png",
                "/**/favicon.ico",
                "/**/internal/**")
    }

    override fun configure(http: HttpSecurity?) {

        if(http != null){
            http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeRequests()
                .antMatchers(projectConfig.adminUserAntMatchers).hasRole(Role.ADMIN)
                .antMatchers("${projectConfig.apiPath}/**")
                .fullyAuthenticated()
        }
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}