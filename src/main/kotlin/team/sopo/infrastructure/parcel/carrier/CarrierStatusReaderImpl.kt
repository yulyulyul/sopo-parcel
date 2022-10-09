package team.sopo.infrastructure.parcel.carrier

import org.springframework.stereotype.Component
import team.sopo.domain.parcel.carrier.CarrierStatus
import team.sopo.domain.parcel.carrier.CarrierStatusReader

@Component
class CarrierStatusReaderImpl(private val repository: CarrierStatusJpaRepository): CarrierStatusReader {
    override fun getAllStatus(): List<CarrierStatus> {
        return repository.findAll().toList()
    }
}