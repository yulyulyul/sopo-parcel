package team.sopo.infrastructure.push

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import team.sopo.common.config.feign.BasicAuthConfiguration

@FeignClient(name = "user-client", url = "\${discovery.user}", configuration = [BasicAuthConfiguration::class])
interface UserClient {
    @GetMapping("api/v1/sopo-user/internal/push-token/{userId}")
    fun getPushToken(@PathVariable("userId") userId: Long): PushToken
}