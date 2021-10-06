package team.sopo.common.config.oauth2

import feign.auth.BasicAuthRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class BasicAuthConfiguration {

    @Value("\${security.oauth2.client.client-id}")
    private lateinit var clientId: String

    @Value("\${security.oauth2.client.client-secret}")
    private lateinit var clientSecret: String

    @Bean
    fun basicAuthRequestInterceptor(): BasicAuthRequestInterceptor{
        return BasicAuthRequestInterceptor(clientId, clientSecret)
    }
}