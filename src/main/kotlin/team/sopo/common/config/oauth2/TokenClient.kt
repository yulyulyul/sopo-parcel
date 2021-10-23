package team.sopo.common.config.oauth2

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam


@FeignClient(name = "token-client",url = "\${sopo.apigateway.url.base}sopo-auth", configuration = [BasicAuthConfiguration::class, TokenErrorDecoder::class])
interface TokenClient {
    @PostMapping("/oauth/check_token", consumes = ["application/x-www-form-urlencoded"])
    fun checkToken(@RequestParam request: Map<String, String>): Map<String, Any>
}