package team.sopo.common.config.feign

import feign.RequestInterceptor
import feign.auth.BasicAuthRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class BasicAuthConfiguration{
    @Bean
    fun basicAuthRequestInterceptor(
        @Value("\${security.oauth2.client.client-id}") clientId: String,
        @Value("\${security.oauth2.client.client-secret}") clientPwd: String
    ): RequestInterceptor{
        return BasicAuthRequestInterceptor(clientId,clientPwd)
    }
}