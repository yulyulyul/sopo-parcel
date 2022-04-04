package team.sopo.infrastructure.parcel.tracker.sopo

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import team.sopo.common.config.feign.DeliveryErrorDecoder
import team.sopo.common.tracing.logging.DeliveryTrackerFeignLogger
import team.sopo.infrastructure.parcel.tracker.TrackingInfo

@FeignClient(
    name = "sopo-tracker",
    url = "\${tracker.sopo.url}",
    configuration = [DeliveryErrorDecoder::class, DeliveryTrackerFeignLogger::class]
)
interface SopoTrackerClient {
    @GetMapping("tracking")
    fun getTrackingInfo(
        @RequestHeader("api-id") apiId: String,
        @RequestParam("carrier") carrier: String,
        @RequestParam("waybillNum") waybillNum: String
    ): TrackingInfo
}