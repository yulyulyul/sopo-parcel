package team.sopo.push

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import team.sopo.common.config.feign.FeignClientConfig
import team.sopo.common.model.api.ApiResult
import team.sopo.push.dto.FcmTokenDTO

@FeignClient(name = "\${discovery.user}",configuration = [FeignClientConfig::class])
interface UserClient {
    @GetMapping("internal/user/{userId}/fcm-token")
    fun getFcmToken(@PathVariable("userId") userId: String): ApiResult<FcmTokenDTO>
}