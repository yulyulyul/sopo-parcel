package team.sopo.parcel.infrastructure

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import team.sopo.common.config.feign.DeliveryErrorDecoder
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

@FeignClient(
    name = "delivery-tracing-service",
    url = "https://apis.tracker.delivery/carriers/",
    configuration = [DeliveryErrorDecoder::class]
)
interface DeliveryTrackerClient {
    @GetMapping("{carrier}/tracks/{waybillNum}")
    fun getTrackingInfo(
        @PathVariable("carrier") carrier: String,
        @PathVariable("waybillNum") waybillNum: String
    ): TrackingInfo
}