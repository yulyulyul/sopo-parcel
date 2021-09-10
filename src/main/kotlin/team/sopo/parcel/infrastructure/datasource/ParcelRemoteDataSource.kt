package team.sopo.parcel.infrastructure.datasource

import team.sopo.parcel.domain.Parcel

interface ParcelRemoteDataSource {
    fun getRefreshedParcel(oldParcel: Parcel, userId: String): Parcel
    fun getParcelFromRemote(carrier: String, waybillNum: String, userId: String, alias: String): Parcel
}