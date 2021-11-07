package team.sopo.common.config.feign

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import team.sopo.parcel.infrastructure.DeliveryClient

@Component
class DeliveryClientFallbackFactory: FallbackFactory<DeliveryClient> {

    private val logger: Logger = LogManager.getLogger(DeliveryClientFallbackFactory::class)

    override fun create(cause: Throwable?): DeliveryClient {

        logger.error("DeliveryClientFallbackFactory is created!!")
        logger.error("error by .. ${cause?.message}")

        return object : DeliveryClient {
            override fun getTrackingInfo(carrier: String, waybillNum: String): TrackingInfo? {
                return null
            }
        }
    }
}