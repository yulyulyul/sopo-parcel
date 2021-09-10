package team.sopo.parcel.infrastructure

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import team.sopo.common.config.feign.DeliveryClientFallbackFactory
import team.sopo.common.config.feign.FeignClientConfig
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

@FeignClient(name = "delivery-tracing-service", url = "https://apis.tracker.delivery/carriers/", fallbackFactory = DeliveryClientFallbackFactory::class , configuration = [FeignClientConfig::class])
interface DeliveryClient {
    @GetMapping("{carrier}/tracks/{waybillNum}")
    fun getTrackingInfo(
        @PathVariable("carrier") carrier: String,
        @PathVariable("waybillNum") waybillNum: String
    ): TrackingInfo?
}