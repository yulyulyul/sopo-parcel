package team.sopo.parcel.infrastructure

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import team.sopo.common.config.feign.DeliveryErrorDecoder
import team.sopo.common.tracing.logging.DeliveryTrackerFeignLogger
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

@FeignClient(
    name = "delivery-tracing-service",
    url = "https://apis.tracker.delivery/carriers/",
    configuration = [DeliveryErrorDecoder::class, DeliveryTrackerFeignLogger::class]
)
interface DeliveryTrackerClient {
    @GetMapping("{carrier}/tracks/{waybillNum}")
    fun getTrackingInfo(
        @RequestHeader("api-id") apiId: String,
        @PathVariable("carrier") carrier: String,
        @PathVariable("waybillNum") waybillNum: String
    ): TrackingInfo
}