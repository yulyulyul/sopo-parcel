package team.sopo.infrastructure.push

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import team.sopo.common.config.feign.BasicAuthConfiguration
import team.sopo.domain.push.PushInfo

@FeignClient(name = "push-client", url = "\${discovery.user}", configuration = [BasicAuthConfiguration::class])
interface PushClient {
    @PostMapping("api/v1/sopo-push/awake-device")
    fun pushToAwakeDevice(@RequestBody request: PushInfo.AwakeDevice)

    @PostMapping("api/v1/sopo-push/update-parcel")
    fun pushToUpdateParcel(@RequestBody request: PushInfo.UpdateParcel)
}