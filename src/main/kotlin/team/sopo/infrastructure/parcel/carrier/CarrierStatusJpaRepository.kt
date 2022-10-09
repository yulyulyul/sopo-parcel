package team.sopo.infrastructure.parcel.carrier

import org.springframework.data.repository.CrudRepository
import team.sopo.domain.parcel.carrier.CarrierStatus

interface CarrierStatusJpaRepository : CrudRepository<CarrierStatus, String>