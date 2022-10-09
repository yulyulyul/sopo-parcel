package team.sopo.domain.parcel.carrier

interface CarrierStatusReader {
    fun getAllStatus(): List<CarrierStatus>
}