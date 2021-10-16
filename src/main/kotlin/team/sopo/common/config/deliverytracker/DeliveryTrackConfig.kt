package team.sopo.common.config.deliverytracker

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class DeliveryTrackConfig {

    @Value("\${delivery.track.hash-alg}")
    lateinit var hashAlg: String
}