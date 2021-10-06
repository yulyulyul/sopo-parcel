package team.sopo.common.config.oauth2

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

@Configuration
@EnableResourceServer
class ResourceServerConfiguration(private final val tokenClient: TokenClient): ResourceServerConfigurerAdapter() {

    @Value("\${security.oauth2.client.resource-ids}")
    private lateinit var resourceIds: String

    /*
        '인증(Authentication)', '권한(Authorization)' 과정에서 발생한 오류는 RestControllerAdvice 에 걸리지 않음.(내부에 filter chain 때문이라고함)
        따라서 인증과정에서 발생하는 오류들을 처리할 HttpAuthentication 과 권한부여 과정에서 발생하는 오류들을 처리할 Oauth2AccessDeniedHandler 를 정의해야함.
     */
    override fun configure(resources: ResourceServerSecurityConfigurer?) {
        resources?.apply {
            val remoteTokenService = SopoResourceServerTokenService(tokenClient)
            tokenServices(remoteTokenService)
            resourceId(resourceIds)

            accessDeniedHandler(Oauth2AccessDeniedHandler())
            authenticationEntryPoint(HttpAuthenticationEntryPoint())
        }
        resources!!.resourceId(resourceIds)
    }

    override fun configure(http: HttpSecurity?) {
        http ?: throw RuntimeException("Can`t configure HttpSecurity!!")
        http.csrf()
                .disable()
            .anonymous()
                .disable()
            .authorizeRequests()
                .antMatchers("/callback")
                .fullyAuthenticated()
    }
}