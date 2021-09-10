package team.sopo.common.config.deliverytracker

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class DeliveryTrackConfig {

    @Value("\${delivery.track.url.main}")
    lateinit var main: String

    @Value("\${delivery.track.url.sub}")
    lateinit var sub: String

    @Value("\${delivery.track.hash-alg}")
    lateinit var hashAlg: String


}