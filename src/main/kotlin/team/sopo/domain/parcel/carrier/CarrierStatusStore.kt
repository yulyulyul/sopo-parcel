package team.sopo.domain.parcel.carrier

interface CarrierStatusStore {
    fun save(carrierStatus: CarrierStatus): CarrierStatus
}