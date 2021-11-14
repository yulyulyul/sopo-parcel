package team.sopo.parcel.domain

interface ParcelStore {
    fun store(parcel: Parcel): Parcel
}
