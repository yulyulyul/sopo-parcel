package team.sopo.common.config.oauth2

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import team.sopo.common.config.feign.FeignSimpleEncoderConfig

@FeignClient(name = "token-client",url = "\${sopo.apigateway.url.base}sopo-auth", configuration = [BasicAuthConfiguration::class, TokenErrorDecoder::class, FeignSimpleEncoderConfig::class]
)
interface TokenClient {
    @PostMapping("/oauth/check_token")
    fun checkToken(
        @RequestBody request: CheckTokenRequest
    ): Map<String, Any>
}