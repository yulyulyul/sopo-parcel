package team.sopo.common.config.feign

import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import team.sopo.parcel.infrastructure.DeliveryClient

@Component
class DeliveryClientFallbackFactory: FallbackFactory<DeliveryClient> {
    override fun create(cause: Throwable?): DeliveryClient {
        return object : DeliveryClient {
            override fun getTrackingInfo(carrier: String, waybillNum: String): TrackingInfo? {
                return null
            }
        }
    }
}