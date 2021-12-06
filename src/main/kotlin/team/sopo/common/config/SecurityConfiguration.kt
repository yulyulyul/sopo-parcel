package team.sopo.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.SecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler
import team.sopo.common.consts.Role

@Configuration
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration(
    private val projectConfig: ProjectConfig,
    private val roleHierarchy: RoleHierarchy
): WebSecurityConfigurerAdapter() {


    private fun webExpressionHandler(): SecurityExpressionHandler<FilterInvocation?> {
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
                "/api/v1/sopo-parcel/test/**",
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
            .antMatchers(projectConfig.adminUserAntMatchers).hasRole(Role.ADMIN)
            .antMatchers("${projectConfig.apiPath}/**")
            .fullyAuthenticated()
    }
}