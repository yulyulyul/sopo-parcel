package team.sopo.domain.parcel.carrier

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "carrier_status")
class CarrierStatus(
    @Id
    val carrier: String,
    val name: String,
    val available: Boolean,
)