package team.sopo.infrastructure.parcel.carrier

import org.springframework.stereotype.Component
import team.sopo.domain.parcel.carrier.CarrierStatus
import team.sopo.domain.parcel.carrier.CarrierStatusStore

@Component
class CarrierStatusStoreImpl(private val repository: CarrierStatusJpaRepository) : CarrierStatusStore {
    override fun save(carrierStatus: CarrierStatus): CarrierStatus {
        return repository.save(carrierStatus)
    }
}